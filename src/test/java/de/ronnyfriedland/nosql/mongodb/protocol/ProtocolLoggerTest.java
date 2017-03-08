package de.ronnyfriedland.nosql.mongodb.protocol;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.test.context.junit4.SpringRunner;

import com.mongodb.BasicDBObject;

import de.ronnyfriedland.nosql.mongodb.protocol.ProtocolLogger.Status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProtocolLogger.class)
@MockBeans({ @MockBean(Logger.class) })
public class ProtocolLoggerTest {

    @Autowired
    private ProtocolLogger subject;

    @Test
    public void testNoAttributes() {
        subject.doLog(Status.SUCCESS, new BasicDBObject());
    }

    @Test
    public void testWithAttributes() {
        BasicDBObject obj = new BasicDBObject();
        obj.append("1", "this is a test value");
        obj.append("2", "this is another test value");
        subject.doLog(Status.SUCCESS, obj, "1", "2");
    }

}
