package com.smile.seckill.exception;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.smile.seckill.result.CodeMsg;
import com.smile.seckill.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * 处理全局异常的切面定义
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    private static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(value=Exception.class)
	public Result<String> exceptionHandler(HttpServletRequest request, Exception e){
		// e.printStackTrace();
		if(e instanceof GlobalException) {
			GlobalException ex = (GlobalException)e;
			return Result.error(ex.getCm());
		}else if(e instanceof BindException) {
			BindException ex = (BindException)e;
			List<ObjectError> errors = ex.getAllErrors();
			ObjectError error = errors.get(0);
			String msg = error.getDefaultMessage();
			return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
		}else if(e instanceof JedisConnectionException) {
            log.info("JedisConnectionException:" + e.getMessage());
            return Result.error(CodeMsg.SERVER_ERROR);
        }else {
            log.info(e.getMessage());
            return Result.error(CodeMsg.SERVER_ERROR);
		}
	}
}