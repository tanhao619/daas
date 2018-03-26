package com.youedata.daas.rest.modular.model.vo;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by cdyoue on 2017/12/11.
 */
public class TaskListVo {

    /*
    主键id
     */

    private Integer id;
    /*
    任务名称
     */
    private String taskTitle;

    /*
    数据源类型
     */
    private String dsType;

    /*
    目标介质信息(json格式)
     */
    private JSONObject mediumInfo;

    /*
    任务性质（1：周期性  2：一次性）
     */
    private Integer taskType;


    /*
    任务状态(1:启用2:停用 3:报错停用）
     */
    private Integer taskStatus;

    /*
    接入任务创建人
     */
    private String creater;

    /*
      创建时间
     */
    private String createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getDsType() {
        return dsType;
    }

    public void setDsType(String dsType) {
        this.dsType = dsType;
    }

    public JSONObject getMediumInfo() {
        return mediumInfo;
    }

    public void setMediumInfo(JSONObject mediumInfo) {
        this.mediumInfo = mediumInfo;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "TaskListVo{" +
                "id=" + id +
                ", taskTitle='" + taskTitle + '\'' +
                ", dsType=" + dsType +
                ", mediumInfo='" + mediumInfo + '\'' +
                ", taskType=" + taskType +
                ", taskStatus=" + taskStatus +
                ", creater='" + creater + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
