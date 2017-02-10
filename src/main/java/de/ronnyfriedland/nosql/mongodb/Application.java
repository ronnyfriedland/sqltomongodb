package de.ronnyfriedland.nosql.mongodb;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.ronnyfriedland.nosql.mongodb.service.MigrationService;

@SpringBootApplication
public class Application {

    @Autowired
    private MigrationService service;

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    private void execute() throws Exception {
        service.doMigration();
    }
}
