package com.youedata.daas.rest.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.plugins.PaginationInterceptor;
import com.youedata.daas.core.mutidatasource.DynamicDataSource;
import com.youedata.daas.core.mutidatasource.annotion.DataSourceType;
import com.youedata.daas.core.mutidatasource.config.MutiDataSourceProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * MybatisPlus配置
 *
 * @author stylefeng
 * @Date 2017年8月23日12:51:41
 */
@Configuration
@EnableTransactionManagement(order = 2)//多数据源让spring事务的aop在多数据源aop后面
@MapperScan(basePackages = {"com.youedata.daas.rest.**.dao"})
public class MybatisPlusConfig {

    @Autowired
    private MutiDataSourceProperties properties;

    /**
     * 另外一个数据源
     * @return
     */
    private DruidDataSource otherDatasource(){
        DruidDataSource source = new DruidDataSource();
        source.setMaxActive(200);
        source.setInitialSize(20);
        properties.datasourceTwoConfig(source);
        return source;
    }

    /**
     * 主数据源
     * @return
     */
    private DruidDataSource defaultDatasource(){
        DruidDataSource source = new DruidDataSource();
        source.setMaxActive(200);
        source.setInitialSize(20);
        properties.datasourceOneConfig(source);
        return source;
    }

    /**
     * 多数据源连接池
     */
    @Bean
    public DynamicDataSource mutiDatasource(){
        DruidDataSource defaultDatasource = defaultDatasource();
        DruidDataSource otherDatasource = otherDatasource();
        try {
            defaultDatasource.init();
            otherDatasource.init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DynamicDataSource dataSource = new DynamicDataSource();
        HashMap<Object, Object> sourceMap = new HashMap<Object, Object>();
        sourceMap.put(DataSourceType.def, defaultDatasource);
        sourceMap.put(DataSourceType.other, otherDatasource);
        dataSource.setTargetDataSources(sourceMap);
        dataSource.setDefaultTargetDataSource(defaultDatasource);
        return dataSource;
    }

    /**
     * mybatis-plus分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }


}
