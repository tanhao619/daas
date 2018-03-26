package com.youedata.daas.rest.modular.model.vo;

import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by cdyoue on 2017/12/11.
 */
public class DataSourceVo {

    private Integer id;
    /*
    数据源类型（1,TABLE 2,FILE 3,STREAM）
     */
    private Integer dsType;

    /*
    数据源名称
     */
    private String dsTitle;

    /*
    数据源描述
     */
    private String dsDesc;

    //  引擎（1,ORACLE,2,MYSQL,3,FTP,4,API)
    private Integer engine;
    /*
    创建人
     */
    private String creater;

    /*
    Mysql,oracle,api,ftp数据源信息(url,userName,passWord,limitIp,port)
     */
    @JsonProperty("dsInfo")
    private String dsInfo;

    public Integer getDsType() {
        return dsType;
    }

    public void setDsType(Integer dsType) {
        this.dsType = dsType;
    }

    public String getDsTitle() {
        return dsTitle;
    }

    public void setDsTitle(String dsTitle) {
        this.dsTitle = dsTitle;
    }

    public String getDsDesc() {
        return dsDesc;
    }

    public void setDsDesc(String dsDesc) {
        this.dsDesc = dsDesc;
    }

    public String getDsInfo() {
        return dsInfo;
    }

    public void setDsInfo(String dsInfo) {
        this.dsInfo = dsInfo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEngine() {
        return engine;
    }

    public void setEngine(Integer engine) {
        this.engine = engine;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }
}
