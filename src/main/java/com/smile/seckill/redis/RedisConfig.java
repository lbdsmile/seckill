package com.smile.seckill.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Redis的配置信息
 */
@Data
@Component
@ConfigurationProperties(prefix = "redis")
public class RedisConfig {
    private String host; // Redis 服务ip地址
    private int port; // Redis 服务端口号
    private int timeout; // Redis 连接超时时间,单位 秒s
    private String password; // Redis 登录密码
    private int poolMaxTotal; // Redis 连接池中总连接的最大数量
    private int poolMaxIdle; // Redis 连接池中空闲连接的最大数量
    private int poolMaxWait;// Redis 获取连接的最大等待时间,单位 秒s
}