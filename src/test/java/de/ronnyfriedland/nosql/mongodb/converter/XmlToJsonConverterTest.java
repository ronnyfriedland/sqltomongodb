package de.ronnyfriedland.nosql.mongodb.converter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = XmlToJsonConverter.class)
public class XmlToJsonConverterTest {

    @Autowired
    private XmlToJsonConverter subject;

    @Test
    public void testNullValue() {
        Assert.assertNull(subject.convert(null));
    }

    @Test
    public void testValidValue() throws Exception {
        String testXml = "<?xml version='1.0'?><foo><bar>123</bar><bar>abc</bar></foo>";

        String testJson = subject.convert(testXml);
        Assert.assertNotNull(testJson);
        Assert.assertEquals("{\"foo\":{\"bar\":[123,\"abc\"]}}", testJson);
    }

}
