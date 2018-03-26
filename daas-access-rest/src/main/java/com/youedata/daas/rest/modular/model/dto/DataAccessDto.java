package com.youedata.daas.rest.modular.model.dto;

/**
 * Created by Tanhao on 2018/1/12.
 */
public class DataAccessDto {
    private Long yAxis = 0L;
    private String xAxis = "";//日期
    private Integer totalSize;

    public Long getyAxis() {
        return yAxis;
    }

    public void setyAxis(Long yAxis) {
        this.yAxis = yAxis;
    }

    public String getxAxis() {
        return xAxis;
    }

    public void setxAxis(String xAxis) {
        this.xAxis = xAxis;
    }

    public Integer getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Integer totalSize) {
        this.totalSize = totalSize;
    }
}
