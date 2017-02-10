package de.ronnyfriedland.nosql.mongodb.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * @author ronnyfriedland
 */
@Configuration
public class EntityConfiguration {

    @Bean
    public Entity entity() throws Exception {
        XmlMapper xmlMapper = new XmlMapper();
        Entity entity = xmlMapper.readValue(Thread.currentThread().getContextClassLoader().getResource("import.xml"),
                Entity.class);
        return entity;
    }
}
