package com.youedata.daas.rest.config;

import com.alibaba.fastjson.JSONObject;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

/**
 * 数据单元接口
 * Created by cdyoue on 2018/1/5.
 */
@Configuration
@ConfigurationProperties(prefix = "daas-meta")
public class DaasMetaConfig {
   private String resCreate;
   private String relationPut;
   private String kinsPut;
    private String proCreate;
    private String resDetail;
    private String resDel;
    private String labelGet;

    public String getResCreate() {
        return resCreate;
    }

    public void setResCreate(String resCreate) {
        this.resCreate = resCreate;
    }

    public String getRelationPut() {
        return relationPut;
    }

    public void setRelationPut(String relationPut) {
        this.relationPut = relationPut;
    }

    public String getKinsPut() {
        return kinsPut;
    }

    public void setKinsPut(String kinsPut) {
        this.kinsPut = kinsPut;
    }

    public String getProCreate() {
        return proCreate;
    }

    public void setProCreate(String proCreate) {
        this.proCreate = proCreate;
    }

    public String getResDetail() {
        return resDetail;
    }

    public void setResDetail(String resDetail) {
        this.resDetail = resDetail;
    }

    public String getResDel() {
        return resDel;
    }

    public void setResDel(String resDel) {
        this.resDel = resDel;
    }

    public String getLabelGet() {
        return labelGet;
    }

    public void setLabelGet(String labelGet) {
        this.labelGet = labelGet;
    }
}
