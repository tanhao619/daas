package com.youedata.daas.rest.modular.service.impl;

import com.google.common.collect.Lists;
import com.youedata.daas.rest.modular.model.DO.JobDetailDO;
import com.youedata.daas.rest.modular.service.IQuartzJobDetailService;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 定时任务操作
 *
 * @author: lucky
 * @since: 2017-8-29 11:32
 */
@Service
@Transactional
public class QuartzJobDetailServiceImpl implements IQuartzJobDetailService {

    // SchedulerFactoryBean 创建
    @Autowired
    private Scheduler scheduler;

    // 任务列表
    @Transactional(readOnly = true)
    @Override
    public List<JobDetailDO> queryJobList() throws Exception{
        List<JobDetailDO> jobDetailDOs = Lists.newArrayList();

        // 数据处理
        Function<Set<JobKey>,List<JobDetailDO>> copyPropFun = jbst -> {
            List<JobDetailDO> jddList = Lists.newArrayList();
            jddList = jbst.stream().map(jk ->{
                JobDetail jd = null;
                List<Trigger> trList = null;
                try {
                    trList = this.getTriggerByKey(jk);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    jd = this.getJobDetailByKey(jk);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // jobDetail
                JobDetailDO jobDetailDO = new JobDetailDO();
                jobDetailDO.fillWithQuartzJobDetail.accept(jd);
                jobDetailDO.fillWithQuartzTriggers.accept(trList);
                return jobDetailDO;
            }).collect(Collectors.toList());
            return jddList;
        };

        try {
            Set<JobKey> jobSet = scheduler.getJobKeys(GroupMatcher.anyJobGroup());
            jobDetailDOs = copyPropFun.apply(jobSet);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return jobDetailDOs;
    }

    /**
     * 查询指定jobkey jobDetail
     * @param jobKey
     * @return
     */
    @Transactional(readOnly = true)
    @Override
    public JobDetailDO queryByKey(JobKey jobKey) throws Exception{
        JobDetailDO jobDetailDO = new JobDetailDO();
        JobDetail jobDetail = this.getJobDetailByKey(jobKey);
        if (Objects.nonNull(jobDetail)) {
            List<Trigger> triggerList = this.getTriggerByKey(jobKey);
            jobDetailDO.fillWithQuartzJobDetail.accept(jobDetail);
            jobDetailDO.fillWithQuartzTriggers.accept(triggerList);
        }
        return jobDetailDO;
    }

    /**
     * 添加任务
     * @param jobDetailDO
     */
    @Override
    public void add(JobDetailDO jobDetailDO) throws Exception{
        JobDetail jobDetail = jobDetailDO.getJobDO().convert2QuartzJobDetail();
        Set<CronTrigger> triggerSet = jobDetailDO.getTriggerDOs().stream().map(jtd ->
            jtd.convert2QuartzTrigger(jobDetail)
        ).collect(Collectors.toSet());

        // 如果已经存在 则替换
        scheduler.scheduleJob(jobDetail,triggerSet,true);
    }

    /**
     * 删除任务
     *
     * @param jobKeyList
     */
    @Override
    public void remove(List<JobKey> jobKeyList) throws Exception{
        scheduler.deleteJobs(jobKeyList);
    }

    /**
     * 删除任务
     *
     * @param jobKey
     */
    @Override
    public void remove(JobKey jobKey) throws Exception{
        scheduler.deleteJob(jobKey);
    }

    // 停用任务
    @Override
    public void disable(GroupMatcher<JobKey> matcher) throws Exception{
        scheduler.pauseJobs(matcher);
    }

    // 停用任务
    @Override
    public void disable(JobKey jobKey) throws Exception{
        scheduler.pauseJob(jobKey);
    }

    // 停用所有任务
    @Override
    public void disableAll() throws Exception{
        scheduler.pauseAll();
    }

    // 启用任务
    @Override
    public void enable(GroupMatcher<JobKey> matcher) throws Exception{
        scheduler.resumeJobs(matcher);
    }

    // 启用任务
    @Override
    public void enable(JobKey jobKey) throws Exception{
        scheduler.resumeJob(jobKey);
    }

    // 启用所有任务
    @Override
    public void enableAll() throws Exception{
        scheduler.resumeAll();
    }

    // 立即触发任务
    @Override
    public void triggerNow(JobKey jobKey, JobDataMap jobDataMap) throws Exception{
        scheduler.triggerJob(jobKey,jobDataMap);
    }

    // 立即触发任务
    @Override
    public void triggerNow(JobKey jobKey) throws Exception{
        scheduler.triggerJob(jobKey);
    }

    // 启动任务
    @Override
    public void start() throws Exception{
        scheduler.start();
    }

    // 停止任务
    @Override
    public void shutdown() throws Exception{
        scheduler.shutdown();
    }

    /**
     * 根据key 获取jobDetail
     * @param jobKey
     * @return
     */
    @Transactional(readOnly = true)
    @Override
    public JobDetail getJobDetailByKey(JobKey jobKey) throws Exception{
        return scheduler.getJobDetail(jobKey);
    }

    /**
     * 根据key 获取job trigger
     * @param jobKey
     * @return
     */
    @Override
    public List<Trigger> getTriggerByKey(JobKey jobKey) throws Exception{
        return (List<Trigger>)scheduler.getTriggersOfJob(jobKey);
    }
}
