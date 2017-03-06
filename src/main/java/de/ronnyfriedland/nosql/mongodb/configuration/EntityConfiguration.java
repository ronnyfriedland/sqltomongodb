package de.ronnyfriedland.nosql.mongodb.configuration;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * @author ronnyfriedland
 */
@Configuration
public class EntityConfiguration {

    @Value("${sql.migration.configuration}")
    private String configurationfile;

    @Bean
    public Entity entity() throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        Entity entity = xmlMapper.readValue(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(configurationfile),
                Entity.class);
        return entity;
    }
}
