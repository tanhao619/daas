package com.youedata.daas.rest.modular.model;

import com.baomidou.mybatisplus.annotations.TableName;

import java.util.Date;

/**
 * Created by Tanhao on 2017/12/28.
 */
@TableName("daas_access_warnning")
public class WarnningPo {
    private Integer id;
    //接入任务名称
    private String taskName;
    //数据源类型（MYSQL FILE ORACLE API）
    private Integer dsType;
    //目标介质类型
    private String medium;
    //告警类型:1: 未达到传输标准 2: 周期内未执行 3: 重复文件 4: 报错停止
    private Integer warnningType;
    //告警时间
    private Date createTime;
    //任务id
    private Integer taskId;
    //文件路径(如果是重复文件,不为空)
    private String filePath;
    //文件名(如果是重复文件,不为空)
    private String fileName;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
