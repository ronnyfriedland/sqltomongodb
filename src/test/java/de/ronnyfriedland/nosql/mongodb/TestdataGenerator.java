package de.ronnyfriedland.nosql.mongodb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TestdataGenerator implements ApplicationRunner, DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(TestdataGenerator.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(final ApplicationArguments args) throws Exception {
        Connection c = jdbcTemplate.getDataSource().getConnection();
        DatabaseMetaData dbm = c.getMetaData();
        ResultSet tables = dbm.getTables(null, null, "message", null);
        if (!tables.next()) {
            LOG.info("Table 'message' not found - creating it.");
            jdbcTemplate
            .execute("create table message (id varchar(255) primary key, subject varchar(2048), data blob)");
        }

        PreparedStatement s = jdbcTemplate.getDataSource().getConnection().prepareStatement("insert into message values(?, ?, ?)");
        for (int i = 0; i < 100; i++) {
            s.setString(1, String.valueOf(i));
            s.setString(2, "test subject " + i);
            s.setBytes(3, ("Hello BLOB " + i).getBytes());
            s.execute();
        }
        LOG.info("Testdata created.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() throws Exception {
        jdbcTemplate.execute("drop table message");
        LOG.info("Table 'message' dropped.");
    }
}
