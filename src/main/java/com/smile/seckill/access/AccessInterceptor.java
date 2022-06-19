package com.smile.seckill.access;

import java.io.OutputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.smile.seckill.domain.SeckillUser;
import com.smile.seckill.redis.AccessKey;
import com.smile.seckill.redis.RedisService;
import com.smile.seckill.result.CodeMsg;
import com.smile.seckill.result.Result;
import com.smile.seckill.service.SeckillUserService;

/**
 * 接口限流的注解逻辑实现
 */
@Service
public class AccessInterceptor extends HandlerInterceptorAdapter{
	
	@Autowired
	SeckillUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if(handler instanceof HandlerMethod) {
			SeckillUser user = getUser(request, response);
			UserContext.setUser(user);
			HandlerMethod hm = (HandlerMethod)handler;
			AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
			if(accessLimit == null) {
				return true;
			}
			int seconds = accessLimit.seconds();
			int maxCount = accessLimit.maxCount();
			boolean needLogin = accessLimit.needLogin();
			String key = request.getRequestURI();
			if(needLogin) {
				if(user == null) {
					render(response, CodeMsg.SESSION_ERROR);
					return false;
				}
				key += "_" + user.getId();
			}
			AccessKey accessKey = AccessKey.withExpire(seconds);
			Integer count = redisService.get(accessKey, key, Integer.class);
	    	if(count  == null) {
	    		 redisService.set(accessKey, key, 1);
	    	}else if(count < maxCount) {
	    		 redisService.incr(accessKey, key);
	    	}else {
	    		render(response, CodeMsg.ACCESS_LIMIT_REACHED);
	    		return false;
	    	}
		}
		return true;
	}

    /**
     * 返回错误信息
     * @param response
     * @param cm
     * @throws Exception
     */
	private void render(HttpServletResponse response, CodeMsg cm)throws Exception {
		response.setContentType("application/json;charset=UTF-8");
		OutputStream out = response.getOutputStream();
		String str  = JSON.toJSONString(Result.error(cm));
		out.write(str.getBytes("UTF-8"));
		out.flush();
		out.close();
	}

    /**
     * 通过请求设置用户信息
     * @param request
     * @param response
     * @return
     */
	private SeckillUser getUser(HttpServletRequest request, HttpServletResponse response) {
		String paramToken = request.getParameter(SeckillUserService.COOKIE_NAME_TOKEN);
		String cookieToken = getCookieValue(request, SeckillUserService.COOKIE_NAME_TOKEN);
		if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
			return null;
		}
		String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
		return userService.getUserInfoByToken(response, token);
	}

    /**
     * 通过 cookie 名字，获取到 cookie 的值
     * @param request
     * @param cookieName
     * @return
     */
	private String getCookieValue(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if(cookies == null || cookies.length <= 0){
			return null;
		}
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals(cookieName)) {
				return cookie.getValue();
			}
		}
		return null;
	}
}