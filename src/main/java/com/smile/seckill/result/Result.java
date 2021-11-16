package com.smile.seckill.result;

import lombok.Getter;

/**
 * 通用返回报文定义类
 * @param <T>
 */
@Getter
public class Result<T> {
    private int code; // 错误码
    private String msg; // 错误信息
    private T data; // 返回数据

    /**
     * 成功
     */
    public static <T> Result<T> success(T data){
        return new Result<T>(data);
    }

    /**
     * 失败
     */
    public static <T> Result<T> error(CodeMsg codeMsg){
        return new Result<T>(codeMsg);
    }

    /**
     * 成功的构造函数
     * @param data
     */
    private Result (T data){
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }

    /**
     * 失败的构造函数
     * @param codeMsg
     */
    private Result (CodeMsg codeMsg){
        if (null == codeMsg){
            return;
        }
        this.code = codeMsg.getCode();
        this.msg = codeMsg.getMsg();
        this.data = data;
    }
}