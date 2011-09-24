package com.darinpope.util;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.terracotta.api.ClusteringToolkitExtension;

import java.util.concurrent.BlockingQueue;

public class TerracottaBlockingQueueFactoryBean implements InitializingBean, FactoryBean, DisposableBean {

  @Autowired
  private ClusteringToolkitExtension toolkit;

  private String name;

  private Integer capacity;

  @Required
  public void setName(String name) {
    this.name = name;
  }

  public void setCapacity(Integer capacity) {
    this.capacity = capacity;
  }

  private BlockingQueue<byte[]> queue;

  public Object getObject() throws Exception {
    return queue;
  }

  public Class<?> getObjectType() {
    return (queue != null ? queue.getClass() : BlockingQueue.class);
  }

  public boolean isSingleton() {
    return true;
  }

  public void afterPropertiesSet() throws Exception {
    if(capacity != null) {
      queue = toolkit.getBlockingQueue(name,capacity);
    } else {
      queue = toolkit.getBlockingQueue(name);
    }
  }

  public void destroy() throws Exception {
    if(toolkit != null && toolkit.getClusterInfo() != null && toolkit.getClusterInfo().areOperationsEnabled()) {
      toolkit.unregisterBlockingQueue(name);
    }
  }
}
