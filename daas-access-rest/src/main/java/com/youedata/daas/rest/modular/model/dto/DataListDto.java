package com.youedata.daas.rest.modular.model.dto;

/**
 * Created by Tanhao on 2018/1/9.
 */
public class DataListDto {
    private String type = "";
    private Integer length = 0;
    private Integer space = 0;
    private String startTime = "";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getSpace() {
        return space;
    }

    public void setSpace(Integer space) {
        this.space = space;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
