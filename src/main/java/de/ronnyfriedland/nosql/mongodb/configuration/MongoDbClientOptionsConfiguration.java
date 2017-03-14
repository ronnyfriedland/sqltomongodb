package de.ronnyfriedland.nosql.mongodb.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoClientOptionsFactoryBean;

import com.mongodb.MongoClientOptions;

import de.ronnyfriedland.nosql.mongodb.configuration.ssl.ConfigurableSSLSocketFactory;

@Configuration
public class MongoDbClientOptionsConfiguration extends MongoClientOptionsFactoryBean {

    @Value("${mongodb.timeout.connection}")
    private int connectionTimeout;

    @Value("${mongodb.timeout.read}")
    private int readTimeout;

    @Value("${mongodb.ssl.enabled}")
    private boolean useSsl;

    @Autowired
    private ConfigurableSSLSocketFactory mongoDbSSLSocketFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    protected MongoClientOptions createInstance() throws Exception {
        super.setConnectTimeout(connectionTimeout);
        super.setSocketTimeout(readTimeout);
        super.setSsl(useSsl);
        if (useSsl) {
            super.setSslSocketFactory(mongoDbSSLSocketFactory);
        }

        return super.createInstance();
    }
}
