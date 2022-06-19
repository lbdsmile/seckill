package com.smile.seckill.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.smile.seckill.access.UserContext;
import com.smile.seckill.domain.SeckillUser;

/**
 * 实现参数解析器
 */
@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

//	@Autowired
//	SeckillUserService userService;
	
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> clazz = parameter.getParameterType();
		return clazz == SeckillUser.class;
	}

	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		return UserContext.getUser();
	}
}