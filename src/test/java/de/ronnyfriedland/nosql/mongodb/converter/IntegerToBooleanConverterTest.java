package de.ronnyfriedland.nosql.mongodb.converter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IntegerToBooleanConverter.class)
public class IntegerToBooleanConverterTest {

    @Autowired
    private IntegerToBooleanConverter subject;

    @Test
    public void testNullValue() {
        Assert.assertFalse(subject.convert(null));
    }

    @Test
    public void testFalse() throws Exception {
        Assert.assertFalse(subject.convert(0));
    }

    @Test
    public void testTrue() throws Exception {
        for (int i = 1; i < 10; i++) {
            Assert.assertTrue(subject.convert(i));
        }
    }

}
