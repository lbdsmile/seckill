package com.smile.seckill.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smile.seckill.domain.SeckillOrder;
import com.smile.seckill.domain.SeckillUser;
import com.smile.seckill.domain.OrderInfo;
import com.smile.seckill.redis.SeckillKey;
import com.smile.seckill.redis.RedisService;
import com.smile.seckill.util.MD5Util;
import com.smile.seckill.util.UUIDUtil;
import com.smile.seckill.vo.GoodsVo;

/**
 * 秒杀服务
 */
@SuppressWarnings("restriction")
@Service
public class SeckillService {
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	RedisService redisService;

	/**
	 * 发起秒杀
	 * @param user
	 * @param goods
	 * @return
	 */
	@Transactional
	public OrderInfo seckill(SeckillUser user, GoodsVo goods) {
		//减库存 下订单 写入秒杀订单
		boolean success = goodsService.reduceStock(goods);
		if(success) {
			//order_info maiosha_order
			return orderService.createOrder(user, goods);
		}else {
			setGoodsOver(goods.getId());
			return null;
		}
	}

	/**
	 * 获取秒杀结果
	 * @param userId
	 * @param goodsId
	 * @return
	 */
	public long getSeckillResult(Long userId, long goodsId) {
		SeckillOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
		if(order != null) {//秒杀成功
			return order.getOrderId();
		}else {
			boolean isOver = getGoodsOver(goodsId);
			if(isOver) {
				return -1;
			}else {
				return 0;
			}
		}
	}

	/**
	 * 设置商品
	 * @param goodsId
	 */
	private void setGoodsOver(Long goodsId) {
		redisService.set(SeckillKey.isGoodsOver, "" + goodsId, true);
	}

	/**
	 * 获取商品状态
	 * @param goodsId
	 * @return
	 */
	private boolean getGoodsOver(long goodsId) {
		return redisService.exists(SeckillKey.isGoodsOver, "" + goodsId);
	}

	/**
	 * 重置商品
	 * @param goodsList
	 */
	public void reset(List<GoodsVo> goodsList) {
		goodsService.resetStock(goodsList);
		orderService.deleteOrders();
	}

	/**
	 * 检查路径
	 * @param user
	 * @param goodsId
	 * @param path
	 * @return
	 */
	public boolean checkPath(SeckillUser user, long goodsId, String path) {
		if(user == null || path == null) {
			return false;
		}
		String pathOld = redisService.get(SeckillKey.getSeckillPath, ""+user.getId() + "_"+ goodsId, String.class);
		return path.equals(pathOld);
	}

	/**
	 * 创建秒杀路径
	 * @param user
	 * @param goodsId
	 * @return
	 */
	public String createSeckillPath(SeckillUser user, long goodsId) {
		if(user == null || goodsId <=0) {
			return null;
		}
		String str = MD5Util.md5(UUIDUtil.getUuid() + "123456");
    	redisService.set(SeckillKey.getSeckillPath, ""+user.getId() + "_"+ goodsId, str);
		return str;
	}

	/**
	 * 创建验证码
	 * @param user
	 * @param goodsId
	 * @return
	 */
	public BufferedImage createVerifyCode(SeckillUser user, long goodsId) {
		if(user == null || goodsId <=0) {
			return null;
		}
		int width = 80;
		int height = 32;
		//create the image
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		// set the background color
		g.setColor(new Color(0xDCDCDC));
		g.fillRect(0, 0, width, height);
		// draw the border
		g.setColor(Color.black);
		g.drawRect(0, 0, width - 1, height - 1);
		// create a random instance to generate the codes
		Random rdm = new Random();
		// make some confusion
		for (int i = 0; i < 50; i++) {
			int x = rdm.nextInt(width);
			int y = rdm.nextInt(height);
			g.drawOval(x, y, 0, 0);
		}
		// generate a random code
		String verifyCode = generateVerifyCode(rdm);
		g.setColor(new Color(0, 100, 0));
		g.setFont(new Font("Candara", Font.BOLD, 24));
		g.drawString(verifyCode, 8, 24);
		g.dispose();
		//把验证码存到redis中
		int rnd = calc(verifyCode);
		redisService.set(SeckillKey.getSeckillVerifyCode, user.getId()+","+goodsId, rnd);
		//输出图片	
		return image;
	}

	/**
	 * 校验验证码
	 * @param user
	 * @param goodsId
	 * @param verifyCode
	 * @return
	 */
	public boolean checkVerifyCode(SeckillUser user, long goodsId, int verifyCode) {
		if(user == null || goodsId <=0) {
			return false;
		}
		Integer codeOld = redisService.get(SeckillKey.getSeckillVerifyCode, user.getId()+","+goodsId, Integer.class);
		if(codeOld == null || codeOld - verifyCode != 0 ) {
			return false;
		}
		redisService.delete(SeckillKey.getSeckillVerifyCode, user.getId()+","+goodsId);
		return true;
	}

	/**
	 * 计算验证码结果
	 * @param exp
	 * @return
	 */
	private static int calc(String exp) {
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("JavaScript");
			return (Integer)engine.eval(exp);
		}catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private static char[] ops = new char[] {'+', '-', '*'};

	/**
	 * 生成验证码内容
	 * @param rdm
	 * @return
	 */
	private String generateVerifyCode(Random rdm) {
		int num1 = rdm.nextInt(10);
	    int num2 = rdm.nextInt(10);
		int num3 = rdm.nextInt(10);
		char op1 = ops[rdm.nextInt(3)];
		char op2 = ops[rdm.nextInt(3)];
		String exp = ""+ num1 + op1 + num2 + op2 + num3;
		return exp;
	}
}