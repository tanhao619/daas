package com.youedata.daas.rest.modular.model;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * <p>
 * 生产日志信息实体
 * </p>
 */
@TableName("daas_production_info")
public class ProdLogPo {
    /**
     * 主键id
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /**
     * 接入数据集ID
     */
    private String resId;

    /**
     * 写入流量（mb)/秒
     */
    private Integer flowrate;

    /**
     * 写入记录数（条数）
     */
    private Integer records;

    /**
     * 写入总量（mb）
     */
    private Integer totalSize;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 任务id
     */
    private Integer taskId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public Integer getFlowrate() {
        return flowrate;
    }

    public void setFlowrate(Integer flowrate) {
        this.flowrate = flowrate;
    }

    public Integer getRecords() {
        return records;
    }

    public void setRecords(Integer records) {
        this.records = records;
    }

    public Integer getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Integer totalSize) {
        this.totalSize = totalSize;
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

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }
}