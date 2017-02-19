package de.ronnyfriedland.nosql.mongodb.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProtocolLogger {

    private static final Logger LOG = LoggerFactory.getLogger(ProtocolLogger.class);

    public static enum Status {

        SUCCESS("SUCCESS"), ERROR("ERROR");

        private final String status;

        private Status(final String status) {
            this.status = status;
        }
    }

    public void doLog(final Status status, final Object value) {
        LOG.info("[{}]: {}", status.status, value);
    }
}
