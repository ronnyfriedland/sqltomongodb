package de.ronnyfriedland.nosql.mongodb.converter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StringToArrayConverter.class)
public class StringToArrayConverterTest {

    @Autowired
    private StringToArrayConverter subject;

    @Test
    public void testNullValue() {
        Assert.assertNull(subject.convert(null));
    }

    @Test
    public void testValidValue() throws Exception {
        String testValue = "i am a test";
        Assert.assertTrue(subject.convert(testValue).length == 1);
        Assert.assertEquals(testValue, subject.convert(testValue)[0]);
    }

    @Test
    public void testValidValues() throws Exception {
        String testValue1 = "i am a test";
        String testValue2 = "i am another test";
        String testValue = testValue1 + "," + testValue2;
        Assert.assertEquals(2, subject.convert(testValue).length);
        Assert.assertEquals(testValue1, subject.convert(testValue)[0]);
        Assert.assertEquals(testValue2, subject.convert(testValue)[1]);
    }

}
