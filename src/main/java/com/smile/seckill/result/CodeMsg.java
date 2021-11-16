package com.smile.seckill.result;

import lombok.Getter;

/**
 * 通用错误码定义
 */
@Getter
public class CodeMsg {

    private int code; // 错误码
    private String msg; // 错误信息

    // 通用异常
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
    // 登录模块 5002XX
    // 商品模块 5003XX
    // 订单模块 5004XX
    // 秒杀模块 5005XX

    private CodeMsg(int code, String msg){
        this.code = code;
        this.msg = msg;
    }
}