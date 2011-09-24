package com.darinpope.util;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.terracotta.api.ClusteringToolkitExtension;
import org.terracotta.collections.ClusteredMap;


public class TerracottaMapFactoryBean implements InitializingBean, FactoryBean, DisposableBean {

    @Autowired
    private ClusteringToolkitExtension toolkit;

    private String name;

    @Required
    public void setName(String name) {
      this.name = name;
    }

    ClusteredMap<Object,Object> clusteredMap;

    public Object getObject() throws Exception {
        return clusteredMap;
    }

    public Class<?> getObjectType() {
        return (clusteredMap != null ? clusteredMap.getClass() : ClusteredMap.class);
    }

    public boolean isSingleton() {
        return true;
    }

    public void afterPropertiesSet() throws Exception {
        clusteredMap = toolkit.getMap(name);
    }

    public void destroy() throws Exception {
    }
}
