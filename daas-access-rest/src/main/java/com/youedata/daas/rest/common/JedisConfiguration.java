package com.youedata.daas.rest.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan// 解决 Configuration 注解中使用 Autowired 注解 IDE 报错
public class JedisConfiguration {
    @Autowired
    RedisConfig redisConfig;

    @Bean
    public ShardedJedisPool convertJedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(redisConfig.getMaxTotal());
        jedisPoolConfig.setMaxIdle(redisConfig.getMaxIdle());
        jedisPoolConfig.setMaxWaitMillis(redisConfig.getMaxWaitMillis());
        jedisPoolConfig.setTestOnBorrow(redisConfig.getTestOnBorrow());
        List<JedisShardInfo> jedisShardInfoList = new ArrayList<>();
        jedisShardInfoList.add(new JedisShardInfo(redisConfig.getUrl()));
        return new ShardedJedisPool(jedisPoolConfig, jedisShardInfoList);
    }
}

