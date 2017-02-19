package de.ronnyfriedland.nosql.mongodb.configuration;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@SpringBootTest
public class EntityConfigurationTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private EntityConfiguration subject;

    @Test
    public void testBean() throws Exception {
        Assert.assertNotNull(subject);
        Assert.assertNotNull(subject.entity());
    }

    @Test
    public void testConfig() throws Exception {
        Entity entity = subject.entity();
        Assert.assertNotNull(entity.getSourceSql());
        Assert.assertEquals("message", entity.getTargetCollection());

        Assert.assertEquals(3, entity.getColumn().size());
        // id
        Assert.assertEquals(true, entity.getColumn().get(0).isIdentityField());
        Assert.assertEquals("string", entity.getColumn().get(0).getType());
        // subject
        Assert.assertEquals(false, entity.getColumn().get(1).isIdentityField());
        Assert.assertEquals("string", entity.getColumn().get(1).getType());
        // data
        Assert.assertEquals(false, entity.getColumn().get(2).isIdentityField());
        Assert.assertEquals("blob", entity.getColumn().get(2).getType());
        Assert.assertEquals(true, entity.getColumn().get(2).isStoreInGridfs());
    }
}
