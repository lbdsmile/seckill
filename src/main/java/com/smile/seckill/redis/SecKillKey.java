package com.smile.seckill.redis;

/**
 * 秒杀 key 定义
 */
public class SecKillKey extends BasePrefix{

	private SecKillKey( int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	public static SecKillKey isGoodsOver = new SecKillKey(0, "go");
	public static SecKillKey getSecKillPath = new SecKillKey(60, "mp");
	public static SecKillKey getSecKillVerifyCode = new SecKillKey(300, "vc");
}
