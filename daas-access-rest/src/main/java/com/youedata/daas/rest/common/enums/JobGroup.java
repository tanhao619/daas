package com.youedata.daas.rest.common.enums;

/**
 * Created by cdyoue on 2018/1/16.
 */
public enum  JobGroup {
    GROUP("daas")
    ;
    private String name;

    JobGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
