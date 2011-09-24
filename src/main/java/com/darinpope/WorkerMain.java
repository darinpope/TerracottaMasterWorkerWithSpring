package com.darinpope;

import com.darinpope.processor.Worker;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class WorkerMain {

    private Worker worker;

    @Required
    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public static void main(String[] args) throws Exception {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] {"applicationContext.xml"});
        WorkerMain main = ctx.getBean(WorkerMain.class);
        while(true) {}
    }
}