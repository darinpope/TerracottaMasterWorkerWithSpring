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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@Service
public class Scheduler implements InitializingBean, DisposableBean {
    private static final Logger LOGGER = Logger.getLogger(Scheduler.class);

    @Resource
    private ClusteringToolkitExtension toolkit;

    @Resource
    private Ehcache schedulerInfoCache;

    @Resource
    private Ehcache workerInfoCache;

    @Resource
    private Ehcache jobStatusCache;

    private String queueName;

    private BlockingQueue<byte[]> schedulerQueue;

    private boolean schedulerActive;

    public void setSchedulerActive(boolean schedulerActive) {
        this.schedulerActive = schedulerActive;
    }

    public void processSchedulerQueue() throws Exception {
        while(schedulerActive) {
            byte[] queueData = schedulerQueue.take();
            if(queueData == null) {
                continue;
            }
            String jobId = (String) SerializationUtils.deserialize(queueData);
            JobStatus jobStatus = null;
            Element element = jobStatusCache.get(jobId);
            if(element != null) {
                jobStatus = (JobStatus) element.getValue();
                if("READY".equalsIgnoreCase(jobStatus.getJobState())) {
                    jobStatus.setMasterQueueName(queueName);
                    jobStatusCache.put(new Element(jobId, jobStatus));
                    putJobInWorkerQueue(jobId);
                } else {
                    jobStatus.setJobState("COMPLETE");
                    jobStatusCache.putWithWriter(new Element(jobId,jobStatus));
                    jobStatusCache.remove(jobId);
                }
            }
        }
    }

    public void putJobInWorkerQueue(String jobId) throws Exception {
        BlockingQueue<byte[]> workerQueue = getRandomWorkerQueue();
        workerQueue.put(SerializationUtils.serialize(jobId));
    }

    private BlockingQueue<byte[]> getRandomWorkerQueue() {
        List<String> keys = (List<String>) workerInfoCache.getKeys();
        List<String> cal = new ArrayList<String>(keys);
        Collections.shuffle(cal);
        String key = (String) cal.get(0);
        LOGGER.info("key = " + key);
        NodeInfo nodeInfo = null;
        Element element = workerInfoCache.get(key);
        if(element != null) {
            nodeInfo = (NodeInfo) element.getValue();
        }
        String workerQueueName = nodeInfo.getQueueName();
        LOGGER.info("workerQueueName = " + workerQueueName);
        return toolkit.getBlockingQueue(workerQueueName);
    }

    public void destroy() throws Exception {
        //TODO: deal with jobIds still on the queue and any jobs that were midflight
        schedulerInfoCache.remove(queueName);
        this.setSchedulerActive(false);
    }

    public void afterPropertiesSet() throws Exception {
        String role = System.getProperty("role");
        LOGGER.info("role = " + role);
        if(!StringUtils.equalsIgnoreCase("scheduler",role)) {
            return;
        }
        queueName = System.getProperty("queueName");
        LOGGER.info("queueName = " + queueName);
        if(StringUtils.isBlank(queueName)) {
            throw new Exception("queueName was not set");
        }

        String hostname = InetAddress.getLocalHost().getHostName();
        if(StringUtils.isBlank(hostname)) {
            hostname = InetAddress.getLocalHost().getHostAddress();
        }
        LOGGER.info("hostname = " + hostname);

        LOGGER.info("setting up the scheduler node");

        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setQueueName(queueName);
        nodeInfo.setRole(NodeInfo.Role.SCHEDULER);
        nodeInfo.setHostName(hostname);

        schedulerInfoCache.put(new Element(queueName,nodeInfo));
        schedulerQueue = toolkit.getBlockingQueue(queueName);

        LOGGER.info("universallyUniqueClientID = " + toolkit.getClusterInfo().getUniversallyUniqueClientID());
        this.setSchedulerActive(true);
        this.processSchedulerQueue();
    }
}
