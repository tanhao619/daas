package com.youedata.daas.rest.modular.model;

import java.util.Date;

/**
 * 接入历史
 * Created by cdyoue on 2017/12/17.
 */
public class DataHistory {
    private Integer id;

    //数据集ID
    private String resId;

    //写入流量（mb)/秒
    private Integer flowrate;

    //写入记录数（条数）
    private Integer records;

    //写入总量（mb）
    private Integer totalSize;

    private Date startTime;

    private Date endTime;


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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
