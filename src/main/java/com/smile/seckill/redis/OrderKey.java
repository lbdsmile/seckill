package com.smile.seckill.redis;

/**
 * 订单 key 定义
 */
public class OrderKey extends BasePrefix {

    public OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
