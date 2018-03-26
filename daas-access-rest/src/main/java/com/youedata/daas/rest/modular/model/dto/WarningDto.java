package com.youedata.daas.rest.modular.model.dto;

import java.util.Date;

/**
 * Created by Tanhao on 2017/12/28.
 */
public class WarningDto {
    private Integer id;
    //接入任务名称
    private String taskName;
    //数据源类型（MYSQL FILE ORACLE API）
    private Integer dsType;
    //目标介质类型
    private String medium;
    //告警类型:1: 未达到传输标准 2: 周期内未执行 3: 重复文件 4: 报错停止
    private Integer warnningType;
    //任务状态
    private Integer taskStatus;
    //任务性质（1：周期性  2：一次性）
    private Integer taskType;
    //告警时间
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getDsType() {
        return dsType;
    }

    public void setDsType(Integer dsType) {
        this.dsType = dsType;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public Integer getWarnningType() {
        return warnningType;
    }

    public void setWarnningType(Integer warnningType) {
        this.warnningType = warnningType;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }
}
