package de.ronnyfriedland.nosql.mongodb.protocol;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.mongodb.BasicDBObject;

import de.ronnyfriedland.nosql.mongodb.protocol.ProtocolLogger.Status;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = ProtocolLogger.class)
@PrepareForTest(Logger.class)
public class ProtocolLoggerTest {

    @InjectMocks
    private ProtocolLogger subject;

    @Mock
    private Logger logger;

    @Before
    public void setup() throws Exception {
        PowerMockito.mockStatic(Logger.class);

        Field field = ProtocolLogger.class.getDeclaredField("LOG");
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, logger);
    }

    @Test
    public void testNoAttributes() throws Exception {
        subject.doLog(Status.SUCCESS, new BasicDBObject());

        Mockito.verify(logger, Mockito.times(1)).info("[{}]: {}", "SUCCESS", new BasicDBObject());
    }

    @Test
    public void testWithAttributes() {
        ArgumentCaptor<String> logEntry = ArgumentCaptor.forClass(String.class);

        BasicDBObject obj = new BasicDBObject();
        obj.append("1", "this is a test value");
        obj.append("2", "this is another test value");
        subject.doLog(Status.SUCCESS, obj, "1", "2");

        Mockito.verify(logger, Mockito.times(1)).info(logEntry.capture(), logEntry.capture(), logEntry.capture());

        List<String> logMessages = logEntry.getAllValues();

        Assert.assertThat(logMessages.get(0), CoreMatchers.is("[{}]: {}"));
        Assert.assertThat(logMessages.get(1), CoreMatchers.is("SUCCESS"));
        Assert.assertThat(logMessages.get(2), CoreMatchers.any(String.class));
    }

}
