package com.youedata.daas.rest.modular.model.dto;

/**
 * Created by Tanhao on 2018/1/2.
 */
public class WarningCvsDto {
    private Integer id;
    //接入任务名称
    private String taskName;
    //数据源类型（MYSQL FILE ORACLE API）
    private String dsType;
    //目标介质类型
    private Integer medium;
    //告警类型:1: 未达到传输标准 2: 周期内未执行 3: 重复文件 4: 报错停止
    private Integer warnningType;
    //告警时间
    private String createTime;
    //任务性质
    private String taskType;
    //任务id
    private Integer taskId;

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

    public String getDsType() {
        return dsType;
    }

    public void setDsType(String dsType) {
        this.dsType = dsType;
    }

    public Integer getMedium() {
        return medium;
    }

    public void setMedium(Integer medium) {
        this.medium = medium;
    }

    public Integer getWarnningType() {
        return warnningType;
    }

    public void setWarnningType(Integer warnningType) {
        this.warnningType = warnningType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
}
