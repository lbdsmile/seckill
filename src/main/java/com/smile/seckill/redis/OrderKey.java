package com.smile.seckill.redis;

/**
 * 订单 key 定义
 */
public class OrderKey extends BasePrefix {

    public OrderKey(String prefix) {
        super(prefix);
    }
    public static OrderKey getSecKillOrderByUidGid = new OrderKey("moug");
}
