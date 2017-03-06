package de.ronnyfriedland.nosql.mongodb.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import de.ronnyfriedland.nosql.mongodb.configuration.Entity;
import de.ronnyfriedland.nosql.mongodb.configuration.EntityConfiguration;

/**
 * @author ronnyfriedland
 */
@Service
public class MigrationService {

    private static final Logger LOG = LoggerFactory.getLogger(MigrationService.class);

    @Autowired
    private EntityConfiguration entityConfiguration;

    @Autowired
    private MigrationBatchExecutor migrater;

    @Value("${sql.result.limit}")
    private int limit;

    @Value("${sql.result.offset}")
    private int offset;

    @Value("${sql.batchsize}")
    private int batchsize;

    public void doMigration() {
        // TODO: refactor !
        if (0 > limit)
            limit = Integer.MAX_VALUE;
        else
            limit += offset;

        try {
            Entity entity = entityConfiguration.entity();

            boolean finished = false;
            int currentBatch = 1;
            do {
                String sql = StringUtils.replace(entity.getSourceSql(), "{limit}",
                        String.valueOf(batchsize * currentBatch));
                sql = StringUtils.replace(sql, "{offset}", String.valueOf(offset));

                LOG.info("Execute batch processing with sql '" + sql + "'");
                finished = batchsize > migrater.migrate(sql, entity.getColumn(), entity.getTargetCollection())
                        || limit <= (batchsize * currentBatch);

                offset += batchsize;
                currentBatch++;
            } while (!finished);
        } catch (IOException e) {
            LOG.error("Error reading configuration.", e);
        }
    }

}
