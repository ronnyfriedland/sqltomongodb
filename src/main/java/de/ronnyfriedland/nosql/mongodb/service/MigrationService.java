package de.ronnyfriedland.nosql.mongodb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ronnyfriedland.nosql.mongodb.configuration.Entity;
import de.ronnyfriedland.nosql.mongodb.configuration.EntityConfiguration;
import de.ronnyfriedland.nosql.mongodb.reader.SourceDbReader;

/**
 * @author ronnyfriedland
 */
@Service
public class MigrationService {

    @Autowired
    private EntityConfiguration entityConfiguration;

    @Autowired
    private SourceDbReader dbReader;

    public void doMigration() throws Exception {
        Entity entity = entityConfiguration.entity();
        String sqlToExecute = entity.getSource();
    }

}
