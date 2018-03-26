package com.youedata.daas.rest.common.enums;

/**
 * Created by cdyoue on 2018/1/17.
 */
public enum FileType {
    CSV(3),
    XML(4),
    JSON(2),
    TXT(1)
    ;
    private Integer type;

    FileType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
