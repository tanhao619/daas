package com.youedata.daas.rest.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by cdyoue on 2018/1/8.
 */
@Configuration
public class StreamBatchConfig {
    @Value("${batch_stream_chunk}")
    private Integer chunk;
    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;

    public StreamBatchConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job streamJob(Step streamStep){
        return jobBuilderFactory.get("streamJob")
                .start(streamStep)
                .build();
    }
    @Bean
    @JobScope
    public Step streamStep(){
        return null;//TODO 未实现
    }
}
