package de.ronnyfriedland.nosql.mongodb.reader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SourceDbReader {

    @Autowired
    private JdbcTemplate jdbcTemplate;
}
