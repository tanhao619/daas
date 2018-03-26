package com.youedata.daas.rest.modular.model.DO;

import com.google.common.base.Strings;
import org.quartz.*;

import java.text.ParseException;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * 触发器域
 *
 * @author: lucky
 * @since: 2017-8-29 11:32
 */
public class TriggerDO {

    // trigger info
    private String name;
    private String group;
    private String cronExpression;
    private String description;

    public CronTrigger convert2QuartzTrigger(JobDetail jobDetail){
        CronExpression ce = null;
        try {
            checkArgument(!Strings.isNullOrEmpty(cronExpression),"cronExpression参数非法");
            ce= new CronExpression(this.cronExpression);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withSchedule(CronScheduleBuilder.cronSchedule(ce))
                .withIdentity(this.name,this.group)
                .withDescription(this.description)
                .build();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TriggerDO{");
        sb.append("name='").append(name).append('\'');
        sb.append(", group='").append(group).append('\'');
        sb.append(", cronExpression='").append(cronExpression).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
