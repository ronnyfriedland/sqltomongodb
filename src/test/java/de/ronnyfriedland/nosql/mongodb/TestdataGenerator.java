package de.ronnyfriedland.nosql.mongodb;

import java.sql.PreparedStatement;
import java.util.UUID;

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
        jdbcTemplate.execute(
                "create table IF NOT EXISTS message (id varchar(255) primary key, subject varchar(2048), flag integer, data blob)");

        PreparedStatement s = jdbcTemplate.getDataSource().getConnection()
                .prepareStatement("insert into message values(?, ?, ?, ?)");
        for (int i = 0; i < 100; i++) {
            s.setString(1, UUID.randomUUID().toString());
            s.setString(2, "TEST Subject " + i);
            s.setInt(3, i % 2);
            s.setBytes(4, ("Hello BLOB " + i).getBytes());
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
