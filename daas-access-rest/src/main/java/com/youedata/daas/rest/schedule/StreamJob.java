package com.youedata.daas.rest.schedule;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 流任务
 * Created by cdyoue on 2018/1/3.
 */
public class StreamJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(StreamJob.class);
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("StreamJob");
    }
}
