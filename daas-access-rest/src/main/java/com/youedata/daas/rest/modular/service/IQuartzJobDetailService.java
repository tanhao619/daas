package com.youedata.daas.rest.modular.service;

import com.youedata.daas.rest.modular.model.DO.JobDetailDO;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;

import java.util.List;

/**
 * Created by cdyoue on 2018/1/11.
 */
public interface IQuartzJobDetailService {
    /**
     * 任务列表
     * @return
     */
    public List<JobDetailDO> queryJobList() throws Exception;

    /**
     * 查询指定jobkey jobDetail
     * @param jobKey
     * @return
     */
    public JobDetailDO queryByKey(JobKey jobKey) throws Exception;

    /**
     * 添加任务
     * @param jobDetailDO
     */
    public void add(JobDetailDO jobDetailDO) throws Exception;

    /**
     * 删除任务
     *
     * @param jobKeyList
     */
    public void remove(List<JobKey> jobKeyList) throws Exception;

    /**
     * 删除任务
     *
     * @param jobKey
     */
    public void remove(JobKey jobKey) throws Exception;

    /**
     * 停用任务
     * @param matcher
     * @return
     */
    public void disable(GroupMatcher<JobKey> matcher) throws Exception;

    /**
     * 停用任务
     * @param jobKey
     * @return
     */
    public void disable(JobKey jobKey) throws Exception;

    /**
     * 通用任务
     * @return
     */
    public void disableAll() throws Exception;

    /**
     * 停用任务
     * @param matcher
     * @return
     */
    public void enable(GroupMatcher<JobKey> matcher) throws Exception;

    /**
     * 停用任务
     * @param jobKey
     * @return
     */
    public void enable(JobKey jobKey) throws Exception;

    /**
     * 停用所有任务
     * @return
     */
    public void enableAll() throws Exception;

    /**
     * 立即触发任务
     * @param jobKey
     * @param jobDataMap
     * @return
     */
    public void triggerNow(JobKey jobKey, JobDataMap jobDataMap) throws Exception;

    /**
     * 立即触发任务
     * @param jobKey
     * @return
     */
    public void triggerNow(JobKey jobKey) throws Exception;

    /**
     * 启用任务
     * @return
     */
    public void start() throws Exception;

    /**
     * 停用任务
     * @return
     */
    public void shutdown() throws Exception;
    /**
     * 根据key 获取jobDetail
     * @param jobKey
     * @return
     */
    public JobDetail getJobDetailByKey(JobKey jobKey) throws Exception;

    /**
     * 根据key 获取job trigger
     * @param jobKey
     * @return
     */
    public List<Trigger> getTriggerByKey(JobKey jobKey) throws Exception;
}
