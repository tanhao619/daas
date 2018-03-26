package com.youedata.daas.rest.util;

/**
 * Created by cdyoue on 2017/12/13.
 */
public enum SuccessResultEnum {
    SUCCESS(200,"操作成功"),
    ADD_SUCCESS(200,"新增成功"),
    UPDATE_SUCCESS(200,"修改成功"),
    DEL_SUCCESS(200,"删除成功"),
    LINK_SUCCESS(200,"连接成功"),
    START_SUCCESS(200, "启动成功"),
    STOP_SUCCESS(200, "暂停成功"),
    ;
    SuccessResultEnum(int code, String message) {
        this.friendlyCode = code;
        this.friendlyMsg = message;
    }

    private int friendlyCode;

    private String friendlyMsg;

    public int getCode() {
        return friendlyCode;
    }

    public void setCode(int code) {
        this.friendlyCode = code;
    }

    public String getMessage() {
        return friendlyMsg;
    }

    public void setMessage(String message) {
        this.friendlyMsg = message;
    }
}
