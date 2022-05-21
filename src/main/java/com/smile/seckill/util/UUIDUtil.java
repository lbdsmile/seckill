package com.smile.seckill.util;

import java.util.UUID;

/**
 * 生成 通用唯一识别码 Universally Unique Identifier
 */
public class UUIDUtil {
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-","");
    }
}