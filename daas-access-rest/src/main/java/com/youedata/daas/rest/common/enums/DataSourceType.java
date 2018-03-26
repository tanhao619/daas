package com.youedata.daas.rest.common.enums;

/**
 * 数据源类型
 * Created by cdyoue on 2018/1/10.
 */
public enum DataSourceType {

    /**
     * TABLE
     * mysql & oracle
     */
    TABLE(1),
    /**
     * api
     */
    STREAM(3),
    /**
     * FTP
     */
    FILE(2)

    ;
    private Integer type;

    DataSourceType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
