package com.smile.seckill.result;

import lombok.Data;

/**
 * 通用返回报文定义类
 * @param <T>
 */
@Data
public class Result<T> {
    private int code; // 错误码
    private String msg; // 错误信息
    private T data; // 返回数据

    /**
     * 成功
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> success(T data){
        return new Result<T>(data);
    }

    /**
     * 失败
     * @param codeMsg
     * @param <T>
     * @return
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
        if (null != codeMsg){
            this.code = codeMsg.getCode();
            this.msg = codeMsg.getMsg();
        }
    }

    /**
     * 返回特殊返回码与特殊返回信息
     * @param code
     * @param msg
     */
    private Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}