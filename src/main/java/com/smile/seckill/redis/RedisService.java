package com.smile.seckill.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Redis的调用服务封装
 */
@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;

    /**
     * 获取单个对象
     */
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz){
        Jedis jedis = jedisPool.getResource();
        String str = jedis.get(prefix.getPrefix() + key);
        T t = stringToBean(str, clazz);
        return t;
    }

    /**
     * 设置对象
     */
    public <T> boolean set(KeyPrefix prefix, String key, T value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = beanToString(value);
            if (str == null || str.length() <= 0){
                return false;
            }
            // 生成真正的 key
            String realKey = prefix.getPrefix() + key;
            int seconds = prefix.expireSeconds();
            if (seconds <= 0){
                jedis.set(realKey, str);
            }else {
                jedis.setex(realKey, seconds, str);
            }
            return true;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 判断 key 值是否存在
     * @param prefix
     * @param key
     * @return
     */
    public boolean exists(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.exists(prefix.getPrefix() + key);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 增加 key
     * @param prefix
     * @param key
     * @return
     */
    public Long incr(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.incr(prefix.getPrefix() + key);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 减少 key
     * @param prefix
     * @param key
     * @return
     */
    public Long decr(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.decr(prefix.getPrefix() + key);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 将 bean 转换成 String
     * @param value
     * @param <T>
     * @return
     */
    private <T> String beanToString(T value) {
        if (value == null){
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class){
            return "" + value;
        }else if (clazz == String.class){
            return (String)value;
        }else if (clazz == long.class || clazz == Long.class){
            return "" + value;
        }else {
            return JSON.toJSONString(value);
        }
    }

    /**
     * String 转换成 bean
     */
    private <T> T stringToBean(String str, Class<T> clazz){
        if (str == null || str.length() <= 0 || clazz == null){
            return null;
        }
        if (clazz == int.class || clazz == Integer.class){
            return (T) Integer.valueOf(str);
        }else if (clazz == String.class){
            return (T)str;
        }else if (clazz == long.class || clazz == Long.class){
            return (T)Long.valueOf(str);
        }else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }

    /**
     * 释放资源
     * @param jedis
     */
    private void returnToPool(Jedis jedis) {
        if (jedis != null){
            jedis.close();
        }
    }
}