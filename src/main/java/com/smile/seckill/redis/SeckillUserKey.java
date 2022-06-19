package com.smile.seckill.redis;

/**
 * 秒杀用户的 key 定义
 */
public class SeckillUserKey extends BasePrefix {
    public  static final int TOKEN_EXPIRE = 60 * 60 * 24 * 2; // 2天 单位 秒

    public SeckillUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static SeckillUserKey token = new SeckillUserKey(TOKEN_EXPIRE, "tk");
    public static SeckillUserKey getById = new SeckillUserKey(0, "id");
}