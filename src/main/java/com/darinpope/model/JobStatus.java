package com.darinpope.model;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

public class JobStatus implements Serializable {
    private String jobId;
    private String jobState;
    private String masterQueueName;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobState() {
        return jobState;
    }

    public void setJobState(String jobState) {
        this.jobState = jobState;
    }

    public String getMasterQueueName() {
        return masterQueueName;
    }

    public void setMasterQueueName(String masterQueueName) {
        this.masterQueueName = masterQueueName;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this,ToStringStyle.SIMPLE_STYLE, true, true);
    }
}
