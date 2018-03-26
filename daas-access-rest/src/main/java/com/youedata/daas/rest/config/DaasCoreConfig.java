package com.youedata.daas.rest.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created by cdyoue on 2018/1/5.
 */
@Configuration
@ConfigurationProperties(prefix = "daas-core")
public class DaasCoreConfig {
    private String duCreate;
    private String dataPut;
    private String dataInfo;
    private String dataList;
    private String dbName;
    private String hdfsNode;
    private String mysqlNode;
    private String dataReal;
    private String ip;
    private Integer port;
    private String repeat;

    public String getDuCreate() {
        return duCreate;
    }

    public void setDuCreate(String duCreate) {
        this.duCreate = duCreate;
    }

    public String getDataPut() {
        return dataPut;
    }

    public void setDataPut(String dataPut) {
        this.dataPut = dataPut;
    }

    public String getDataInfo() {
        return dataInfo;
    }

    public void setDataInfo(String dataInfo) {
        this.dataInfo = dataInfo;
    }

    public String getDataList() {
        return dataList;
    }

    public void setDataList(String dataList) {
        this.dataList = dataList;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getHdfsNode() {
        return hdfsNode;
    }

    public void setHdfsNode(String hdfsNode) {
        this.hdfsNode = hdfsNode;
    }

    public String getMysqlNode() {
        return mysqlNode;
    }

    public void setMysqlNode(String mysqlNode) {
        this.mysqlNode = mysqlNode;
    }

    public String getDataReal() {
        return dataReal;
    }

    public void setDataReal(String dataReal) {
        this.dataReal = dataReal;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }
}
