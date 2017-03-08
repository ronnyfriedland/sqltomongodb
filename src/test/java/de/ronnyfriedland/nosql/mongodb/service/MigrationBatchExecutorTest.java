package de.ronnyfriedland.nosql.mongodb.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.mongodb.BasicDBList;
import com.mongodb.BulkWriteResult;

import de.ronnyfriedland.nosql.mongodb.MigrationThread;
import de.ronnyfriedland.nosql.mongodb.configuration.Entity;
import de.ronnyfriedland.nosql.mongodb.configuration.EntityConfiguration;

@SpringBootTest
@RunWith(SpringRunner.class)
@MockBeans({ @MockBean(MongoTemplate.class), @MockBean(GridFsTemplate.class), @MockBean(MigrationThread.class) })
public class MigrationBatchExecutorTest {

    @Autowired
    private MigrationBatchExecutor subject;

    @Autowired
    private EntityConfiguration entityConfiguration;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Before
    public void setup() throws Exception {
        BulkOperations bulkOperations = Mockito.mock(BulkOperations.class);
        Mockito.when(bulkOperations.insert(Mockito.any(BasicDBList.class))).thenReturn(bulkOperations);
        BulkWriteResult bulkWriteResult = Mockito.mock(BulkWriteResult.class);
        Mockito.when(bulkWriteResult.getInsertedCount()).thenReturn(50);
        Mockito.when(bulkOperations.execute()).thenReturn(bulkWriteResult);
        Mockito.when(mongoTemplate.bulkOps(Mockito.any(BulkMode.class), Mockito.anyString()))
        .thenReturn(bulkOperations);
    }

    @Test
    public void testMigrate() throws Exception {
        Entity entity = entityConfiguration.entity();

        long result = subject.migrate(entity.getSourceSql(), entity.getColumn(), entity.getTargetCollection());
        Assert.assertEquals(100, result);
    }

}