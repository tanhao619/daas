package com.youedata.daas.rest.modular.model;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;

/**
 * 任务信息实体类
 *
 * @author lucky
 * @create 2017-09-21 14:09
 **/
@ApiModel(value="TaskInfo", description="任务信息类")
public class TaskInfo {

    private String taskId;

    private String taskName;

    private String taskGroup;

    private String taskType;

    private JSONObject taskAccess;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId=taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName=taskName;
    }

    public String getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(String taskGroup) {
        this.taskGroup=taskGroup;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType=taskType;
    }

    public JSONObject getTaskAccess() {
        return taskAccess;
    }

    public void setTaskAccess(JSONObject taskAccess) {
        this.taskAccess=taskAccess;
    }
}
