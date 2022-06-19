package com.smile.seckill.access;

import com.smile.seckill.domain.SeckillUser;

/**
 * 用户上下文定义
 */
public class UserContext {
	
	private static ThreadLocal<SeckillUser> userHolder = new ThreadLocal<SeckillUser>();
	
	public static void setUser(SeckillUser seckillUser) {
		userHolder.set(seckillUser);
	}
	
	public static SeckillUser getUser() {
		return userHolder.get();
	}
}