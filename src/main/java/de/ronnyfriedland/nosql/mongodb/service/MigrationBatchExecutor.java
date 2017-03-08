package de.ronnyfriedland.nosql.mongodb.service;

import java.io.IOException;
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
import org.springframework.data.mongodb.BulkOperationException;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.BulkWriteResult;

import de.ronnyfriedland.nosql.mongodb.configuration.Column;
import de.ronnyfriedland.nosql.mongodb.converter.BlobMimeMessageTextExtractor;
import de.ronnyfriedland.nosql.mongodb.converter.StringToIntegerConverter;
import de.ronnyfriedland.nosql.mongodb.protocol.ProtocolLogger;
import de.ronnyfriedland.nosql.mongodb.protocol.ProtocolLogger.Status;

@Service
@Transactional
public class MigrationBatchExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(MigrationBatchExecutor.class);

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
        long start = System.currentTimeMillis();
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
                    long start = System.currentTimeMillis();
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
                                    value = IOUtils.toByteArray(rs.getBinaryStream(source));
                                } catch (IOException e) {
                                    throw new SQLException("Error copying blob data to byte array", e);
                                }
                            }
                        } else if ("mimemessagetextblob".equals(type)) {
                            value = new BlobMimeMessageTextExtractor().convert(rs.getBinaryStream(source));
                        } else if ("stringtointeger".equals(type)) {
                            value = new StringToIntegerConverter().convert(rs.getString(source));
                        } else if ("integertoboolean".equals(type)) {
                            value = new StringToIntegerConverter().convert(rs.getString(source));
                        } else {
                            throw new IllegalArgumentException("Unknown column datatype " + type);
                        }
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Field {} has value {}.", target, value);
                        }
                        mongoObject.put(target, value);
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Retrieving source data took {} sec.",
                                (System.currentTimeMillis() - start) / 1000);
                    }
                    resultList.add(mongoObject);
                    counter.incrementAndGet();
                }
                return resultList;
            }
        });

        BasicDBList list = new BasicDBList();
        list.addAll(mongoObjects);

        int inserted = 0;
        try {
            BulkWriteResult result = mongoTemplate.bulkOps(BulkMode.ORDERED, collectionName).insert(list).execute();
            inserted = result.getInsertedCount();
        } catch (BulkOperationException e) {
            LOG.error("Migration failed", e);
            inserted = e.getErrors().get(0).getIndex(); // only one element in list because we use ordered mode
        } catch (Exception e) { // we only log unexpected errors
            LOG.error("Unexpected error - migration failed", e);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Migration batch run took {} sec.", (System.currentTimeMillis() - start) / 1000);
        }

        int count = 0;
        for (BasicDBObject mongoObject : mongoObjects) {
            if (count++ < inserted) {
                protocolLogger.doLog(Status.SUCCESS, mongoObject, "_id");
            } else {
                protocolLogger.doLog(Status.ERROR, mongoObject, "_id");
            }
        }

        return counter.longValue();
    }
}
