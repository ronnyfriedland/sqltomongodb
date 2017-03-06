package de.ronnyfriedland.nosql.mongodb.converter;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StringToIntegerConverter.class)
public class StringToIntegerConverterTest {

    @Autowired
    private StringToIntegerConverter subject;

    @Test
    public void testNullValue() {
        Assert.assertNull(subject.convert(null));
    }

    @Test
    public void testValidValue() throws Exception {
        int testvalue = new Random(System.currentTimeMillis()).nextInt();
        Assert.assertEquals(testvalue, subject.convert(String.valueOf(testvalue)).intValue());
    }

}
