package com.youedata.daas.rest.modular.model;

import org.hibernate.validator.constraints.NotEmpty;

import java.util.Date;

/**
 * <p>
 * 告警管理信息对象实体
 * </p>
 *
 * @author chengtao
 */
public class WarnningVo {

    private Integer id;
    private Integer taskId;
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
    //告警时间
    private Date createTime;
    //创建人
    private String creater;
    //任务性质
    private Integer taskType;
    //数据业务组
    private String dataWorks;
    //数据集名称
    private String dataCollects;

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

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getDataWorks() {
        return dataWorks;
    }

    public void setDataWorks(String dataWorks) {
        this.dataWorks = dataWorks;
    }

    public String getDataCollects() {
        return dataCollects;
    }

    public void setDataCollects(String dataCollects) {
        this.dataCollects = dataCollects;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }
}
