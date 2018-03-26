package com.youedata.daas.rest.common.enums;

/**
 * 任务状态
 * Created by cdyoue on 2018/1/10.
 */
public enum TaskStatus {
    /**
     * 启动
     */
    START(1),
    /**
     * 停止
     */
    STOP(2),
    /**
     * 报错
     */
    ERROR(3)
    ;
    private Integer status;

    TaskStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
