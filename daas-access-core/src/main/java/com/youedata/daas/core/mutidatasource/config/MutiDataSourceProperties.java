package com.youedata.daas.core.mutidatasource.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 默认多数据源配置
 *
 * @author fengshuonan
 * @date 2017-08-16 10:02
 */
@Component
@ConfigurationProperties(prefix = "daas")
public class MutiDataSourceProperties {
    private Map<String, String> datasourceOne;

    private Map<String, String> datasourceTwo;

    private void config(DruidDataSource dataSource, Map<String, String> sourceMap) {
        dataSource.setUrl(sourceMap.get("driverClassName"));
        dataSource.setUrl(sourceMap.get("url"));
        dataSource.setUsername(sourceMap.get("username"));
        dataSource.setPassword(sourceMap.get("password"));
    }
    public void datasourceOneConfig(DruidDataSource dataSource){
        config(dataSource, datasourceOne);
    }
    public void datasourceTwoConfig(DruidDataSource dataSource){
        config(dataSource, datasourceTwo);
    }

    public Map<String, String> getDatasourceOne() {
        return datasourceOne;
    }

    public void setDatasourceOne(Map<String, String> datasourceOne) {
        this.datasourceOne = datasourceOne;
    }

    public Map<String, String> getDatasourceTwo() {
        return datasourceTwo;
    }

    public void setDatasourceTwo(Map<String, String> datasourceTwo) {
        this.datasourceTwo = datasourceTwo;
    }
}
