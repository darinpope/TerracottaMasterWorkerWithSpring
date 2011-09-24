package com.darinpope.util;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.cluster.CacheCluster;
import net.sf.ehcache.cluster.ClusterNode;
import net.sf.ehcache.cluster.ClusterScheme;
import net.sf.ehcache.cluster.ClusterTopologyListener;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class TerracottaClusterTopologyListener implements InitializingBean {
    private static final Logger LOGGER = Logger.getLogger(TerracottaClusterTopologyListener.class);

    @Resource
    private CacheManager cacheManager;

    public void afterPropertiesSet() throws Exception {
        CacheCluster cluster = cacheManager.getCluster(ClusterScheme.TERRACOTTA);

        cluster.addTopologyListener( new ClusterTopologyListener() {
            public void nodeJoined(ClusterNode node) {
                LOGGER.info(node.getHostname() + ":" + node.getIp() + ":" + node.getId() + " joined");
            }

            public void nodeLeft(ClusterNode node) {
                LOGGER.info(node.getHostname() + ":" + node.getIp() + ":" + node.getId() + " left");
            }

            public void clusterOnline(ClusterNode node) {
                LOGGER.info(node.getHostname() + ":" + node.getIp() + ":" + node.getId() + " enabled");
            }

            public void clusterOffline(ClusterNode node) {
                LOGGER.info(node.getHostname() + ":" + node.getIp() + ":" + node.getId() + " disabled");
            }

            public void clusterRejoined(ClusterNode oldNode, ClusterNode newNode) {
                LOGGER.info(oldNode.getHostname() + ":" + oldNode.getIp() + ":" + oldNode.getId() + " rejoined the cluster as " + newNode.getHostname() + ":" + newNode.getIp() + ":" + newNode.getId());
            }
        });
    }
}
