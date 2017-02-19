package de.ronnyfriedland.nosql.mongodb;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TestdataGenerator implements ApplicationRunner, DisposableBean {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(final ApplicationArguments args) throws Exception {
        jdbcTemplate.execute("create table message (id varchar(255) primary key, subject varchar(2048), data blob)");

        for (int i = 0; i < 100; i++) {
            jdbcTemplate.execute("insert into message values('" + i + "', 'test subject with id " + i + "', null)");
        }
    }

    @Override
    public void destroy() throws Exception {
        jdbcTemplate.execute("drop table message");
    }
}
