package de.ronnyfriedland.nosql.mongodb.service;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteResult;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;

import de.ronnyfriedland.nosql.mongodb.configuration.Column;
import de.ronnyfriedland.nosql.mongodb.converter.BlobMimeMessageTextExtractor;
import de.ronnyfriedland.nosql.mongodb.converter.StringToArrayConverter;
import de.ronnyfriedland.nosql.mongodb.converter.StringToIntegerConverter;
import de.ronnyfriedland.nosql.mongodb.converter.XmlToJsonConverter;
import de.ronnyfriedland.nosql.mongodb.protocol.ProtocolLogger;
import de.ronnyfriedland.nosql.mongodb.protocol.ProtocolLogger.Status;

/**
 * Batch execution of migration.
 *
 * @author ronnyfriedland
 */
@Service
public class MigrationBatchExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(MigrationBatchExecutor.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GridFS gridFs;

    @Autowired
    private ProtocolLogger protocolLogger;

    /**
     * Migrates the data.
     *
     * @param sql the sql to retrieve the data
     * @param columns the column specification
     * @param collectionName the name of the target collection
     * @param stopOnError if true an exception is raised if error occours, otherwise the error is logged
     * @return number of migrated rows
     */
    @Transactional(readOnly = true)
    public long migrate(final String sql, final Collection<Column> columns, final String collectionName, final boolean stopOnError) {
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
                                if (column.getGridfsId() == null) {
                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug("Parameter 'gridfsIdSourceColumn' not set - generating new uuid for gridfs file");
                                    }
                                    value = UUID.randomUUID().toString();
                                } else {
                                    // if set we use the value of the source column
                                    value = rs.getString(column.getGridfsId());
                                }
                                // uuid as reference in document collection
                                        GridFSInputFile file = gridFs.createFile(rs.getBinaryStream(source));
                                file.put("_id", value);
                                file.save();
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
                        } else if ("xmltojson".equals(type)) {
                            value = new XmlToJsonConverter().convert(rs.getString(source));
                        } else if ("stringtoarray".equals(type)) {
                            value = new StringToArrayConverter(Optional.of(column.getArrayDelimiter()))
                                    .convert(rs.getString(source));
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
        if(stopOnError && inserted < counter.intValue()) {
            throw new IllegalStateException("Migration failed - check logfile for details.");
        }

        return counter.longValue();
    }
}
