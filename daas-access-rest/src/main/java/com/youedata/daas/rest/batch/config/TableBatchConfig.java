package com.youedata.daas.rest.batch.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONObject;
import com.youedata.daas.rest.batch.listener.DaasJobExecutionListener;
import com.youedata.daas.rest.batch.listener.TableItemListener;
import com.youedata.daas.rest.batch.reader.*;
import com.youedata.daas.rest.batch.writer.TableCurserWriter;
import com.youedata.daas.rest.common.JedisClient;
import com.youedata.daas.rest.common.enums.DbType;
import com.youedata.daas.rest.exception.BizExceptionEnum;
import com.youedata.daas.rest.exception.BussinessException;
import com.youedata.daas.rest.modular.service.ICurserService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.AbstractSqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cdyoue on 2018/1/8.
 */
@Configuration
public class TableBatchConfig {
    private Map<JSONObject, DruidDataSource> cache = new HashMap<JSONObject, DruidDataSource>();
    @Value("${batch_table_chunk}")
    private Integer chunk;
    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private ICurserService curserService;
    @Autowired
    private JedisClient jedisClient;

    public TableBatchConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job tableJob(Step tableStep) {
        return jobBuilderFactory.get("tableJob")
                .listener(jobListener())
                .start(tableStep)
                .build();
    }

    @Bean
    @JobScope
    public Step tableStep(
            @Value("#{jobParameters['sourceType']}") String sourceType,
            @Value("#{jobParameters['targetType']}") String targetType,
            @Value("#{jobParameters['threshold']}") Long threshold
    ) {
        return stepBuilderFactory.get("tableStep")
                .listener(new TableItemListener())
                .<JSONObject, JSONObject>chunk(chunk)
                .reader(tableITtemReader(null, null, null, null,null,null,null))
                .writer(tableItemWriter(null, null, 0, null, null, null))
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    @StepScope
    public TablePageItemReaderExt tableITtemReader(
            @Value("#{jobParameters['dsInfo']}") String dsInfo,
            @Value("#{jobParameters['tableName']}") String tableName,
            @Value("#{jobParameters['sql']}") String sql,
            @Value("#{jobParameters['taskId']}") Long taskId,
            @Value("#{jobParameters['pageSize']}") Long pageSize,
            @Value("#{jobParameters['tableTypeName']}") String tableTypeName,
            @Value("#{jobParameters['schames']}") String schames
    ) {
        TablePageItemReaderExt reader = new TablePageItemReaderExt();
        JSONObject dsInfoJson = JSONObject.parseObject(dsInfo);
        //初始化一个数据源
        DruidDataSource dataSource = null;
        if (cache.containsKey(dsInfoJson)) {
            dataSource = cache.get(dsInfoJson);
        } else {
            dataSource = new DruidDataSource();
            dataSource.setDriverClassName(dsInfoJson.getString("driverClassName"));
            dataSource.setUrl(dsInfoJson.getString("url"));
            dataSource.setUsername(dsInfoJson.getString("userName"));
            dataSource.setPassword(dsInfoJson.getString("passWord"));
            cache.put(dsInfoJson, dataSource);
        }
        reader.setPageSize(Integer.parseInt(pageSize.toString()));
        reader.setDataSource(dataSource);
        reader.setTableName(tableName);
        reader.setTableTypeName(tableTypeName);
        reader.setPageSize(Integer.parseInt(pageSize.toString()));
        AbstractSqlPagingQueryProvider queryProvider = null;
        if(DbType.Mysql.getTableTypeName().equals(tableTypeName)){
            queryProvider = new MysqlQueryProvider();
        }else if(DbType.Oracle.getTableTypeName().equals(tableTypeName)){
            queryProvider = new OracleQueryProvider();
        }else{
            throw new BussinessException(BizExceptionEnum.TASK_REQUEST_RAPRAMS_ERROR);
        }
        queryProvider.setSelectClause("*") ;
        queryProvider.setFromClause(tableName);
        Map<String,Order> map = new HashMap<>();
        map.put(schames.split(",")[0],Order.ASCENDING);
        queryProvider.setSortKeys(map);
        reader.setQueryProvider(queryProvider);
        reader.setRowMapper(new TableRowMapper(tableName, dsInfoJson.getString("url")));

        return reader;
    }

    @Bean
    @StepScope
    public TableCurserWriter tableItemWriter(
            @Value("#{jobParameters['mtableName']}") String tableName,
            @Value("#{jobParameters['targetType']}") String targetType,
            @Value("#{jobParameters['unit']}") long unit,
            @Value("#{jobParameters['to']}") String to,
            @Value("#{jobParameters['accessType']}") String accessType,
            @Value("#{jobParameters['taskId']}") String taskId
    ) {
        return new TableCurserWriter(targetType, tableName, unit, to, accessType, taskId);
    }

    @Bean
    public DaasJobExecutionListener jobListener() {
        return new DaasJobExecutionListener();
    }
}
