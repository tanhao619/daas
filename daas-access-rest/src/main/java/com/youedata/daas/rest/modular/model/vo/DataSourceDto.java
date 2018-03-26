package com.youedata.daas.rest.modular.model.vo;

import java.util.Date;

/**
 * Created by cdyoue on 2017/12/11.
 */
public class DataSourceDto {

    /*
    主键id
     */
    private Integer id;

    /*
    数据源类型（1,TABLE 2,FILE 3,STREAM）
     */
    private String dsType;

    /*
    数据源名称
     */
    private String dsTitle;

    /*
    数据源描述
     */
    private String dsDesc;

    /*
    Mysql,oracle,api,ftp数据源信息
     */
    private String dsInfo;

    /*
    创建人
     */
    private String creater;

    /*
    创建时间
     */
    private Date createTime;

    /*
    修改时间
     */
    private Date updateTime;

    //是否关联任务(返回关联任务的数量)
    private Integer usage;

    //  引擎（1,ORACLE,2,MYSQL,3,FTP,4,API）
    private String engine;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDsType() {
        return dsType;
    }

    public void setDsType(String dsType) {
        this.dsType = dsType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
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

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public Integer getUsage() {
        return usage;
    }

    public void setUsage(Integer usage) {
        this.usage = usage;
    }

    @Override
    public String toString() {
        return "DataSourceDto{" +
                "id=" + id +
                ", dsType=" + dsType +
                ", dsTitle='" + dsTitle + '\'' +
                ", dsDesc='" + dsDesc + '\'' +
                ", dsInfo='" + dsInfo + '\'' +
                ", creater='" + creater + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", usage=" + usage +
                '}';
    }
}
