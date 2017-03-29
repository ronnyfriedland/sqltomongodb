package de.ronnyfriedland.nosql.mongodb.configuration.ssl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = de.ronnyfriedland.nosql.mongodb.configuration.ssl.MongoDbSSLSocketFactoryConfiguration.class)
public class MongoDbSSLSocketFactoryConfigurationTest {

    @Autowired
    private MongoDbSSLSocketFactoryConfiguration subject;

    @Test
    public void testCiphersuites() throws Exception {
        ConfigurableSSLSocketFactory socketFactory = subject.mongoDbSSLSocketFactory();
        Assert.assertNotNull(socketFactory);
        String[] configuredCipherSuites = socketFactory.getSupportedCipherSuites();
        Assert.assertNotNull(configuredCipherSuites);
        Assert.assertArrayEquals(new String[] { "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384" }, configuredCipherSuites);
    }
}
