package com.youedata.daas.rest.config.ftppool;

import com.youedata.ftppool.FtpClientFactory;
import com.youedata.ftppool.FtpClientPoolManager;
import com.youedata.ftppool.pool.FtpClientPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ftpclient连接池配置
 *
 * @author lucky
 * @create 2017-10-17 14:53
 **/
@Configuration
public class FtpPoolConfig {

    @Bean
    public FtpClientPoolManager ftpClientPoolManager() {
        FtpClientPoolManager manager = new FtpClientPoolManager();
        manager.setConfig(ftpClientPoolConfig());
        manager.setFactory(ftpClientFactory());
        return manager;
    }

    @Bean
    public FtpClientFactory ftpClientFactory() {
        return new FtpClientFactory();
    }

    @Bean
    public FtpClientPoolConfig ftpClientPoolConfig() {
        FtpClientPoolConfig config = new FtpClientPoolConfig();
        config.setMaxTotal("2000");
        config.setMaxTotalPerKey("2000");
        config.setMaxIdlePerKey("20");
        config.setMinIdlePerKey("5");
        return config;
    }
}
