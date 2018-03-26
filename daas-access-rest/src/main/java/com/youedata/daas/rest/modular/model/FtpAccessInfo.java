package com.youedata.daas.rest.modular.model;

import io.swagger.annotations.ApiModel;

/**
 * FTP接入对象实体类
 *
 * @author lucky
 * @create 2017-09-12 13:23
 **/
@ApiModel(value="FtpAccessInfo", description="FTP接入对象类")
public class FtpAccessInfo extends TaskInfo{

    private String ip;

    private int port;

    private String user;

    private String password;

    private String rootPath;

    private String accPath;

    private String colRule;

    private Integer resId;

    private String accSavPath;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip=ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port=port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user=user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password=password;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath=rootPath;
    }

    public String getAccPath() {
        return accPath;
    }

    public void setAccPath(String accPath) {
        this.accPath=accPath;
    }

    public String getColRule() {
        return colRule;
    }

    public void setColRule(String colRule) {
        this.colRule=colRule;
    }

    public Integer getResId() {
        return resId;
    }

    public void setResId(Integer resId) {
        this.resId = resId;
    }

    public String getAccSavPath() {
        return accSavPath;
    }

    public void setAccSavPath(String accSavPath) {
        this.accSavPath=accSavPath;
    }

    @Override
    public String toString() {
        return "FtpAccessInfo{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", rootPath='" + rootPath + '\'' +
                ", accPath='" + accPath + '\'' +
                ", colRule='" + colRule + '\'' +
                ", resId='" + resId + '\'' +
                ", accSavPath='" + accSavPath + '\'' +
                '}';
    }
}
