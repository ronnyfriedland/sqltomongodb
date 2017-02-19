package de.ronnyfriedland.nosql.mongodb.service;

import java.io.IOException;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DuplicateKeyException;

import de.ronnyfriedland.nosql.mongodb.configuration.Column;
import de.ronnyfriedland.nosql.mongodb.protocol.ProtocolLogger;
import de.ronnyfriedland.nosql.mongodb.protocol.ProtocolLogger.Status;

@Service
@Transactional
public class MigrationExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(MigrationExecutor.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private ProtocolLogger protocolLogger;

    /**
     * Migrates the data.
     *
     * @param sql the sql to retrieve the data
     * @param columns the column specification
     * @param collectionName the name of the target collection
     * @return number of migrated rows
     */
    public long migrate(final String sql, final Collection<Column> columns, final String collectionName) {
        final AtomicInteger counter = new AtomicInteger(0);
        Collection<BasicDBObject> mongoObjects = jdbcTemplate.query(sql,
                new ResultSetExtractor<Collection<BasicDBObject>>() {
            /**
             * {@inheritDoc}
             */
            @Override
            public Collection<BasicDBObject> extractData(final ResultSet rs)
                    throws SQLException, DataAccessException {

                Collection<BasicDBObject> resultList = new ArrayList<>();

                while (rs.next()) {
                    BasicDBObject mongoObject = new BasicDBObject();

                    for (Column column : columns) {
                        String source = column.getSourceColumn();
                        String target = column.getTargetField();
                        String type = column.getType().toLowerCase();

                        Object value;
                        if ("string".equals(type)) {
                            value = rs.getString(source);
                        } else if ("long".equals(type)) {
                            value = rs.getLong(source);
                        } else if ("integer".equals(type)) {
                            value = rs.getInt(source);
                        } else if ("date".equals(type)) {
                            value = rs.getDate(source);
                        } else if ("timestamp".equals(type)) {
                            value = rs.getTimestamp(source);
                        } else if ("boolean".equals(type)) {
                            value = rs.getBoolean(source);
                        } else if ("blob".equals(type)) {
                            if (column.isStoreInGridfs()) {
                                // save uuid of metadata as reference in document
                                value = gridFsTemplate
                                        .store(rs.getBinaryStream(source),
                                                BasicDBObjectBuilder.start()
                                                .add("id", UUID.randomUUID().toString()).get())
                                        .getMetaData().get("id");
                            } else {
                                try {
                                    Blob blob = rs.getBlob(source);
                                    value = IOUtils.toByteArray(blob.getBinaryStream());
                                } catch (IOException e) {
                                    throw new SQLException("Error copying blob data to byte array", e);
                                }
                            }
                        } else {
                            throw new IllegalArgumentException("Unknown column datatype " + type);
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Field {} has value {}.", target, value);
                        }
                        mongoObject.put(target, value);
                        counter.incrementAndGet();
                    }
                    resultList.add(mongoObject);
                }
                return resultList;
            }
        });

        DBCollection collection = mongoTemplate.getCollection(collectionName);
        for (BasicDBObject mongoObject : mongoObjects) {
            try {
                collection.insert(mongoObject);
                protocolLogger.doLog(Status.SUCCESS, mongoObject.getString("_id"));
            } catch (DuplicateKeyException e) {
                protocolLogger.doLog(Status.ERROR, mongoObject.getString("_id"));
                LOG.error("Migration failed: " + mongoObject.getString("_id"), e);
            }
        }
        return counter.longValue();
    }
}
