package com.youedata.daas.rest.common.enums;

/**
 * Created by Administrator on 2018/3/5.
 */
public enum AccessType {
    FILE("1"),
    TABLE("2");

    private String type;

    AccessType(String type) {
        this.type = type;
    }

    public String getAccessType() {

        return type;
    }
}
