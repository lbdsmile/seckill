package com.smile.seckill.redis;

/**
 * 秒杀用户的 key 定义
 */
public class SecKillUserKey extends BasePrefix {
    public  static final int TOKEN_EXPIRE = 60 * 60 * 24 * 2; // 2天 单位 秒

    public SecKillUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static SecKillUserKey token = new SecKillUserKey(TOKEN_EXPIRE, "tk");
}