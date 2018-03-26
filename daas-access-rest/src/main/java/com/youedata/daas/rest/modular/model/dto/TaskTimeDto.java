package com.youedata.daas.rest.modular.model.dto;

/**
 * Created by Tanhao on 2018/1/8.
 */
public class TaskTimeDto {
    private String createTime = "";
    private String startTime = "";
    private String endTime = "";
    private String avrageTime = "";
    private Integer count;
    private Integer taskStatus;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getAvrageTime() {
        return avrageTime;
    }

    public void setAvrageTime(String avrageTime) {
        this.avrageTime = avrageTime;
    }
}
