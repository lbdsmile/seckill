package com.smile.seckill.redis;

/**
 * 定义前缀的接口类
 */
public interface KeyPrefix {
    public int expireSeconds();

    public String getPrefix();
}