package com.youedata.daas.rest.common.enums;

/**
 * Created by cdyoue on 2018/1/10.
 */
public enum JobType {
    TABLE_JOB("TABLEJOB"),
    STREAM_JOB("STREAMJOB"),
    FILE_JOB("FILEJOB")
    ;
    private String name;

    JobType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
