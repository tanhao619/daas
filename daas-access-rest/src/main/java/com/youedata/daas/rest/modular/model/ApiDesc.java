package com.youedata.daas.rest.modular.model;

/**
 * Created by chengtao on 2017/10/23 0018.
 */
public class ApiDesc {
    /**
     * URL链接
     */
    private String url;

    /**
     * TOPIC
     */
    private String kafkaTopic;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public void setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
    }
}
