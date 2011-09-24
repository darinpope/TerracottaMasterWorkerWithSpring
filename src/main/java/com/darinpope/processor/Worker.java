package com.darinpope.processor;

import com.darinpope.model.JobStatus;
import com.darinpope.model.NodeInfo;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.terracotta.api.ClusteringToolkitExtension;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;

@Service
public class Worker implements InitializingBean, DisposableBean {
    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(Worker.class);

    @Resource
    private ClusteringToolkitExtension toolkit;

    @Resource
    private Ehcache workerInfoCache;

    @Resource
    private Ehcache schedulerInfoCache;

    @Resource
    private Ehcache jobStatusCache;

    private BlockingQueue<byte[]> workerQueue;

    private boolean workerActive;

    private String nodeName;

    public void setWorkerActive(boolean workerActive) {
        this.workerActive = workerActive;
    }

    public void processWorkerQueue() throws Exception {
        while(workerActive) {
            byte[] queueData = workerQueue.take();
            if(queueData == null) {
                continue;
            }
            String jobId = (String) SerializationUtils.deserialize(queueData);
            LOGGER.info("jobId = " + jobId);
            JobStatus jobStatus = null;
            Element element =  jobStatusCache.get(jobId);
            if(element != null) {
                jobStatus = (JobStatus) element.getValue();
                jobStatus.setJobState("INPROCESS");
                jobStatusCache.put(new Element(jobId,jobStatus));
                BlockingQueue<byte[]> q = getSchedulerQueue(jobStatus.getMasterQueueName());
                if(q != null) {
                    q.put(SerializationUtils.serialize(jobId));
                }
            }
        }
    }

    private BlockingQueue<byte[]> getSchedulerQueue(String masterQueueName) {
        Element element = schedulerInfoCache.get(masterQueueName);
        if(element != null) {
            return toolkit.getBlockingQueue(masterQueueName);
        }
        //TODO: if here, try to pick another scheduler queue to put the work on
        return null;
    }


    public void destroy() throws Exception {
        this.setWorkerActive(false);
        workerInfoCache.remove(nodeName);
    }

    public void afterPropertiesSet() throws Exception {
        String role = System.getProperty("role");
        if(!StringUtils.equalsIgnoreCase("worker",role)) {
            return;
        }

        String queueName = System.getProperty("queueName");
        if(StringUtils.isBlank(queueName)) {
            throw new Exception("queueName was not set");
        }

        nodeName = System.getProperty("nodeName");
        if(StringUtils.isBlank(nodeName)) {
            throw new Exception("nodeName was not set");
        }

        String hostname = InetAddress.getLocalHost().getHostName();
        if(StringUtils.isBlank(hostname)) {
            hostname = InetAddress.getLocalHost().getHostAddress();
        }

        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setQueueName(queueName);
        nodeInfo.setRole(NodeInfo.Role.WORKER);
        nodeInfo.setHostName(hostname);
        nodeInfo.setNodeName(nodeName);

        workerInfoCache.put(new Element(queueName, nodeInfo));
        workerQueue = toolkit.getBlockingQueue(queueName);

        this.setWorkerActive(true);
        this.processWorkerQueue();
    }
}
