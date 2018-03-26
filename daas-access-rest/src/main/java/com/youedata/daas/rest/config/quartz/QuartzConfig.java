package com.youedata.daas.rest.config.quartz;

import com.youedata.daas.rest.config.ExternalPathConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * Quartz配置类
 *
 * @author: lucky
 * @since: 2017-8-29 11:32
 */
@Configuration
public class QuartzConfig {
    private static final Logger log = LoggerFactory.getLogger(QuartzConfig.class);

    @Autowired
    private ExternalPathConfig externalPathConfig;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private AutowiringQuartzJobFactory autowiringQuartzJobFactory;

    //被@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，并且只会被服务器执行一次。
    // PostConstruct在构造函数之后执行,init()方法之前执行。PreDestroy（）方法在destroy()方法执行执行之后执行
    @PostConstruct
    public void initDone() {
        log.info("Quartz init done...");
    }

    @Bean
    public SchedulerFactoryBean init(){
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setDataSource(dataSource);
        schedulerFactoryBean.setTransactionManager(platformTransactionManager);
        schedulerFactoryBean.setQuartzProperties(externalPathConfig.quartzCfg());

        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setStartupDelay(5);

        // 覆盖已存在定时任务
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(false);

        schedulerFactoryBean.setJobFactory(autowiringQuartzJobFactory);
        return schedulerFactoryBean;
    }

}
