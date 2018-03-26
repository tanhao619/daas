package com.youedata.daas.rest.common.enums;

/**
 * 告警类型
 * Created by cdyoue on 2018/1/8.
 */
public enum WarnningType {
    /**
     * 未达到传输标准
     */
    TRANSMISSION_NOT_REACHED(1),
    /**
     * 周期内未执行
     */
    NOT_IMPLEMENTED_DURING_CYCLE(2),
    /**
     * 重复文件
     */
    REPEAT_FILE(3),
    /**
     * 报错停止
     */
    STOP_BY_ERROR(4)
    ;
    private Integer type;

    WarnningType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
