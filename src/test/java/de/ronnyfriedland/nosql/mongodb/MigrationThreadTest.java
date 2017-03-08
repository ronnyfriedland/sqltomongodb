package de.ronnyfriedland.nosql.mongodb;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.test.context.junit4.SpringRunner;

import de.ronnyfriedland.nosql.mongodb.service.MigrationService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MigrationThread.class)
@MockBeans({ @MockBean(MigrationService.class) })
public class MigrationThreadTest {

    @Autowired
    private MigrationService migrationService;

    @Test
    public void testThread() {
        Mockito.verify(migrationService, Mockito.times(1)).doMigration();
    }
}
