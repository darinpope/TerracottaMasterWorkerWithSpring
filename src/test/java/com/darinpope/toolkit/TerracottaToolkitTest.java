package com.darinpope.toolkit;

import net.sf.ehcache.cluster.CacheCluster;
import net.sf.ehcache.cluster.ClusterScheme;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.terracotta.api.ClusteringToolkit;

import java.util.concurrent.BlockingQueue;

@ContextConfiguration("classpath:applicationContextTest.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TerracottaToolkitTest {

    private ClusteringToolkit toolkit;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        CacheCluster cacheCluster = Mockito.mock(CacheCluster.class);
        Mockito.when(cacheCluster.getScheme()).thenReturn(ClusterScheme.TERRACOTTA);
        ClusteringToolkit toolkit = Mockito.mock(ClusteringToolkit.class);
    }

    @Test
    public void testBlockingQueue() throws Exception {
        BlockingQueue<byte[]> testQueue = toolkit.getBlockingQueue("testQueue");
    }


}
