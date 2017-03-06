package de.ronnyfriedland.nosql.mongodb.protocol;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;

@Service
public class ProtocolLogger {

    private static final Logger LOG = LoggerFactory.getLogger(ProtocolLogger.class);

    private final ObjectMapper mapper = new ObjectMapper();

    public static enum Status {

        SUCCESS("SUCCESS"), ERROR("ERROR");

        private final String status;

        private Status(final String status) {
            this.status = status;
        }
    }

    public void doLog(final Status status, final BasicDBObject value) {
        LOG.info("[{}]: {}", status.status, value);
    }

    public void doLog(final Status status, final BasicDBObject value, final String... attributes) {
        Map<String, Object> attributesToLog = new TreeMap<>();
        for (String attribute : attributes) {
            attributesToLog.put(attribute, value.get(attribute));
        }

        String valueToLog;
        try {
            valueToLog = mapper.writeValueAsString(attributesToLog);
        } catch (JsonProcessingException e) {
            LOG.error("Error creating protocol log for restricted attributes - logging whole object instead.");
            valueToLog = value.toJson();
        }
        LOG.info("[{}]: {}", status.status, valueToLog);
    }
}
