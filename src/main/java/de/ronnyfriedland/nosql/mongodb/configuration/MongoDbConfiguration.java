package de.ronnyfriedland.nosql.mongodb.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoClientOptionsFactoryBean;

import com.mongodb.MongoClientOptions;

@Configuration
public class MongoDbConfiguration extends MongoClientOptionsFactoryBean {

    @Value("${mongodb.timeout.connection}")
    private int connectionTimeout;

    @Value("${mongodb.timeout.read}")
    private int readTimeout;

    /**
     * {@inheritDoc}
     */
    @Override
    protected MongoClientOptions createInstance() throws Exception {
        super.setConnectTimeout(connectionTimeout);
        super.setSocketTimeout(readTimeout);
        return super.createInstance();
    }

}
