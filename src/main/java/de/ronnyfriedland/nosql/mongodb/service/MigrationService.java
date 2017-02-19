package de.ronnyfriedland.nosql.mongodb.service;

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
    private MigrationExecutor migrater;

    @Value("${sql.result.limit}")
    private int limit;

    @Value("${sql.result.offset}")
    private int offset;

    @Value("${sql.batchsize}")
    private int batchsize;

    public void doMigration() throws Exception {
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
    }

}
