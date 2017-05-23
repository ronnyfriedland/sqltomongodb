package de.ronnyfriedland.nosql.mongodb.configuration.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.gridfs.GridFS;

/**
 * @author ronnyfriedland
 */
@Configuration
public class MongoDbConfiguration {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    public GridFS gridFs() {
        return new GridFS(mongoTemplate.getDb());
    }

}
