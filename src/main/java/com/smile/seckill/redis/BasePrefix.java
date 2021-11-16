package com.smile.seckill.redis;

/**
 * 定义前缀的基础抽象类
 */
public abstract class BasePrefix implements KeyPrefix {

    private int expireSeconds; // 过期时间, 单位秒, 0 代表永不过期

    private String prefix; // 前缀

    public BasePrefix(String prefix){
        this(0, prefix);
    }

    public BasePrefix(int expireSeconds, String prefix){
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    public int expireSeconds(){
        return expireSeconds;
    }

    public String getPrefix(){
        String className = getClass().getSimpleName();
        return className + ":" +prefix;
    }

}