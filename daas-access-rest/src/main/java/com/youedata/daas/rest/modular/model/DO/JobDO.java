package com.youedata.daas.rest.modular.model.DO;

import com.youedata.daas.rest.common.AppConst;
import com.youedata.daas.rest.common.enums.JobType;
import com.youedata.daas.rest.schedule.*;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.core.jmx.JobDataMapSupport;
import org.slf4j.Logger;
import org.springframework.util.ClassUtils;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 作业DO
 *
 * @author: lucky
 * @since: 2017-8-29 11:32
 */
public class JobDO {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(JobDO.class);
    private static final Map<String,Class<? extends Job>> SUPPORTED_JOB_TYPES =
            new HashMap<String,Class<? extends Job>>(){
        {
            put(JobType.FILE_JOB.getName(), FileJob.class);
            put(JobType.STREAM_JOB.getName(), StreamJob.class);
            put(JobType.TABLE_JOB.getName(), TableJob.class);
        }
    };
    private static final Set<String> SUPPORTED_EXT_FIELDS = new HashSet<String>(){
        {
            add("type");    // AppConst.JobType
            add("access");   // 接入对象，json类型
        }
    };

    // job info
    private String name;
    private String group;
    private String targetClass;
    private String description;

    // ext info
    // supportExtFields
    private Map<String,Object> extInfo;

    public JobDetail convert2QuartzJobDetail(){
        Class<? extends Job> clazz = null;

        // 如果未定义 则根据extInfo里type获取默认处理类
        if (Objects.isNull(this.targetClass)) {
            String type = String.valueOf(this.extInfo.get("type"));
            clazz = SUPPORTED_JOB_TYPES.get(type);
            checkNotNull(clazz,"未找到匹配type的Job");
            this.targetClass = clazz.getCanonicalName();
        }
        try {
            clazz = (Class<Job>) ClassUtils.resolveClassName(this.targetClass, this.getClass().getClassLoader());
        } catch (IllegalArgumentException e) {
            log.error("加载类错误",e);
        }

        return JobBuilder.newJob()
                .ofType(clazz)
                .withIdentity(this.name,this.getGroup())
                .withDescription(this.description)
                .setJobData(JobDataMapSupport.newJobDataMap(this.extInfo))
                .build();
    }

    public Map<String, Object> getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(Map<String, Object> extInfo) {
        this.extInfo = extInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description=description;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("JobDO{");
        sb.append("name='").append(name).append('\'');
        sb.append(", group='").append(group).append('\'');
        sb.append(", targetClass='").append(targetClass).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", extInfo=").append(extInfo);
        sb.append('}');
        return sb.toString();
    }
}
