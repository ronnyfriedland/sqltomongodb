package de.ronnyfriedland.nosql.mongodb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // @PostConstruct
    // private void execute() throws Exception {
    // ExecutorService es = Executors.newFixedThreadPool(1);
    // try {
    // es.execute(new Runnable() {
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public void run() {
    // try {
    // service.doMigration();
    // } catch (Exception e) {
    // throw new RuntimeException(e);
    // }
    // }
    // });
    // } finally {
    // es.shutdown();
    // }
    // }
}
