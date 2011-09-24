package com.darinpope;

import com.darinpope.model.JobStatus;
import com.darinpope.model.NodeInfo;
import com.darinpope.processor.Scheduler;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.terracotta.api.ClusteringToolkitExtension;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

@Component
public class SchedulerMain {
    private static final Logger LOGGER = Logger.getLogger(SchedulerMain.class);

    @Resource
    private ClusteringToolkitExtension toolkit;

    @Resource
    private Ehcache schedulerInfoCache;

    @Resource
    private Ehcache jobStatusCache;

    @Resource
    private Ehcache workerInfoCache;

    public static void main(String[] args) throws Exception {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] {"applicationContext.xml"});
        SchedulerMain main = ctx.getBean(SchedulerMain.class);

        List<String> keys = (List<String>) main.schedulerInfoCache.getKeys();
        List<String> cal = new ArrayList<String>(keys);
        Collections.shuffle(cal);
        String schedulerQueueName = cal.get(0);

        BlockingQueue<byte[]> queue = main.toolkit.getBlockingQueue(schedulerQueueName);

        LOGGER.info("now submitting work");
        for(int i=0;i<30;i++) {
            String jobId = UUID.randomUUID().toString();
            LOGGER.info("jobId = " + jobId);
            JobStatus jobStatus = new JobStatus();
            jobStatus.setJobId(jobId);
            jobStatus.setJobState("READY");
            main.jobStatusCache.put(new Element(jobId, jobStatus));
            queue.put(SerializationUtils.serialize(jobId));
            //Thread.sleep(500);
        }
    }
}
