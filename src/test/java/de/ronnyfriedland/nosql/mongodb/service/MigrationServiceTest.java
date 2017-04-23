package de.ronnyfriedland.nosql.mongodb.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.test.context.junit4.SpringRunner;

import de.ronnyfriedland.nosql.mongodb.configuration.Column;
import de.ronnyfriedland.nosql.mongodb.configuration.Entity;
import de.ronnyfriedland.nosql.mongodb.configuration.EntityConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { MigrationService.class, EntityConfiguration.class })
@MockBeans({ @MockBean(MigrationBatchExecutor.class) })
public class MigrationServiceTest {

    @Autowired
    private MigrationService subject;

    @Autowired
    private EntityConfiguration entityConfiguration;

    @Autowired
    private MigrationBatchExecutor migrationBatchExecutorMock;

    @Before
    public void setUp() throws Exception {
        Entity entity = entityConfiguration.entity();

        Mockito.when(migrationBatchExecutorMock.migrate(entity.getSourceSql(), entity.getColumn(),
                entity.getTargetCollection(), false)).thenReturn(100L, 50L, 0L);
    }

    @Test
    public void testMigrate() throws Exception {
        subject.doMigration();

        Mockito.verify(migrationBatchExecutorMock, Mockito.times(2)).migrate(Matchers.anyString(),
                Matchers.anyCollectionOf(Column.class), Matchers.anyString(), Matchers.anyBoolean());
    }
}
