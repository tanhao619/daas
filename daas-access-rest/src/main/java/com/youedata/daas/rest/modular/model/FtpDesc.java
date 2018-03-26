package com.youedata.daas.rest.modular.model;

/**
 * Created by chengtao on 2017/10/23 0018.
 */
public class FtpDesc {
    /**
     * 采集规则
     */
    private String colRule;

    private String rootPath;

    private String accPath;

    public String getColRule() {
        return colRule;
    }

    public void setColRule(String colRule) {
        this.colRule = colRule;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getAccPath() {
        return accPath;
    }

    public void setAccPath(String accPath) {
        this.accPath = accPath;
    }
}
