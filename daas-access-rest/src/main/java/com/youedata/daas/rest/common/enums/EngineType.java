package com.youedata.daas.rest.common.enums;

/**
 * Created by cdyoue on 2018/1/12.
 */
public enum EngineType {
    MYSQL(2),
    ORACLE(1),
    FTP(3),
    API(4)
    ;

    EngineType(Integer type) {
        this.type = type;
    }

    private Integer type;

    public Integer getType() {
        return type;
    }
}
