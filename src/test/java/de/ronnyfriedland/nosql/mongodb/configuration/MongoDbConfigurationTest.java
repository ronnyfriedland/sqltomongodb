package de.ronnyfriedland.nosql.mongodb.configuration;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.mongodb.MongoClientOptions;

/**
 * @author ronnyfriedland
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MongoDbConfiguration.class)
public class MongoDbConfigurationTest {

    @Autowired
    private MongoDbConfiguration subject;

    @Value("${mongodb.timeout.connection}")
    private int connectionTimeout;

    @Value("${mongodb.timeout.read}")
    private int readTimeout;

    @Test
    public void testConnectionTimeout() throws Exception {
        MongoClientOptions instance = subject.createInstance();
        Assert.assertNotNull(instance);
        Assert.assertEquals(connectionTimeout, instance.getConnectTimeout());
    }

    @Test
    public void testReadTimeout() throws Exception {
        MongoClientOptions instance = subject.createInstance();
        Assert.assertNotNull(instance);
        Assert.assertEquals(readTimeout, instance.getSocketTimeout());
    }
}
