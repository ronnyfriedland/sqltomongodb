package de.ronnyfriedland.nosql.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import de.ronnyfriedland.nosql.mongodb.service.MigrationService;

@Component
public class MigrationThread implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private MigrationService service;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent arg0) {
        service.doMigration();
    }

}
