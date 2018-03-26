package com.youedata.daas.rest.modular.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by cdyoue on 2017/12/11.
 */
@TableName("daas_access_data_source")
public class DataSourcePo implements Serializable {

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

    /*
    Mysql,oracle,api,ftp数据源信息(url,userName,passWord,port,parentPath)
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

    // 引擎（1,ORACLE,2,MYSQL,3,FTP,4,API）
    private Integer engine;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDsType() {
        return dsType;
    }

    public void setDsType(Integer dsType) {
        this.dsType = dsType;
    }

    @ApiModelProperty(example = "数据源类型（1：MYSQL  2：FTP 3：ORACLE 4：API）", value = "1")


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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getEngine() {
        return engine;
    }

    public void setEngine(Integer engine) {
        this.engine = engine;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", dsType=" + dsType +
                ", dsTitle='" + dsTitle + '\'' +
                ", dsDesc='" + dsDesc + '\'' +
                ", dsInfo='" + dsInfo + '\'' +
                ", creater='" + creater + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}
