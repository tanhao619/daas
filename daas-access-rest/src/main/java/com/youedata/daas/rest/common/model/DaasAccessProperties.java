package com.youedata.daas.rest.common.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "daasaccess", ignoreUnknownFields = false)
//@PropertySource("file:G:\\youeDataPlatform\\daas-access\\daas-access-rest\\src\\main\\resources\\page.properties")
@PropertySource("classpath:page.properties")
@Component
public class DaasAccessProperties {
   private long pagecount;

    public long getPagecount() {
        return pagecount;
    }

    public void setPagecount(long pagecount) {
        this.pagecount = pagecount;
    }
}
