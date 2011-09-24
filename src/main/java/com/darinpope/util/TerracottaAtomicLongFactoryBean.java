package com.darinpope.util;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.terracotta.api.ClusteringToolkitExtension;
import org.terracotta.util.ClusteredAtomicLong;

public class TerracottaAtomicLongFactoryBean implements InitializingBean, FactoryBean, DisposableBean {

  @Autowired
  private ClusteringToolkitExtension toolkit;

  private String name;

  @Required
  public void setName(String name) {
    this.name = name;
  }

  private ClusteredAtomicLong clusteredAtomicLong;

  public Object getObject() throws Exception {
    return clusteredAtomicLong;
  }

  public Class<?> getObjectType() {
    return (clusteredAtomicLong != null ? clusteredAtomicLong.getClass() : ClusteredAtomicLong.class);
  }

  public boolean isSingleton() {
    return true;
  }

  public void afterPropertiesSet() throws Exception {
    clusteredAtomicLong = toolkit.getAtomicLong(name);
  }

  public void destroy() throws Exception {
    if(toolkit != null && toolkit.getClusterInfo() != null && toolkit.getClusterInfo().areOperationsEnabled()) {
      toolkit.unregisterAtomicLong(name);
    }
  }
}
