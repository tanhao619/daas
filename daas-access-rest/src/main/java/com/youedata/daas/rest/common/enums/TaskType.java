package com.youedata.daas.rest.common.enums;

/**
 * 任务类型
 * Created by cdyoue on 2018/1/10.
 */
public enum  TaskType {
    SINGLE(1),
    CYCLE(2)
    ;

    private Integer type;

    TaskType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
