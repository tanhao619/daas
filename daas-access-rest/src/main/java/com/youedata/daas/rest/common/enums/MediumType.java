package com.youedata.daas.rest.common.enums;

/**
 * Created by cdyoue on 2018/1/16.
 */
public enum  MediumType {
    MYSQL(1),
    HIVE(2),
    HBASE(3),
    HDFS(4),
    ;
    private Integer type;

    MediumType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
