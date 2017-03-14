package de.ronnyfriedland.nosql.mongodb.configuration.ssl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ronnyfriedland
 */
@Configuration
public class MongoDbSSLSocketFactoryConfiguration {

    @Value("${mongodb.ssl.tlsversions}")
    private String[] tlsversions;

    @Value("${mongodb.ssl.ciphersuites}")
    private String[] ciphersuites;

    @Bean(name = "mongoDbSSLSocketFactory")
    public ConfigurableSSLSocketFactory mongoDbSSLSocketFactory() throws IOException {
        ConfigurableSSLSocketFactory factory = new ConfigurableSSLSocketFactory();
        factory.setTlsProtocolVersions(tlsversions);
        factory.setCipherSuites(ciphersuites);
        return factory;
    }
}
