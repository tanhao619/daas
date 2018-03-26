package com.youedata.daas.rest.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Service
public class JedisClient {

    private Logger logger = LoggerFactory.getLogger(JedisClient.class);

    @Autowired
    private ShardedJedisPool shardedJedisPool;

    public String set(String key,String value){
        ShardedJedis jedis = null;
        try{
           jedis = shardedJedisPool.getResource();
           return jedis.set(key,value);
        } catch (Exception e){
            logger.error("Redis保存"+key+"异常"+e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public String get(String key){
        ShardedJedis jedis = null;
        try{
            jedis = shardedJedisPool.getResource();
            return jedis.get(key);
        } catch (Exception e){
            logger.error("Redis查询"+key+"异常"+e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long del(String key){
        ShardedJedis jedis = null;
        try{
            jedis = shardedJedisPool.getResource();
            return jedis.del(key);
        } catch (Exception e){
            logger.error("Redis删除"+key+"异常"+e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public long expire(String key,int second){
        ShardedJedis jedis = null;
        try{
            jedis = shardedJedisPool.getResource();
            return jedis.expire(key,second);
        } catch (Exception e){
            logger.error("Redis设置"+key+"超时时间异常"+e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }
}
