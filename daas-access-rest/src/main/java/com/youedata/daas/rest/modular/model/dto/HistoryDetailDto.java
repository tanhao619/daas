package com.youedata.daas.rest.modular.model.dto;

import java.util.Date;

/**
 * Created by Tanhao on 2018/1/11.
 */
public class HistoryDetailDto {

    private Integer id;
    private String time;
    private String useTime;
    private String type;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUseTime() {
        return useTime;
    }

    public void setUseTime(String useTime) {
        this.useTime = useTime;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
