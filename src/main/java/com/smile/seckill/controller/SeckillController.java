package com.smile.seckill.controller;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smile.seckill.access.AccessLimit;
import com.smile.seckill.domain.SeckillOrder;
import com.smile.seckill.domain.SeckillUser;
import com.smile.seckill.rabbitmq.MQSender;
import com.smile.seckill.rabbitmq.SeckillMessage;
import com.smile.seckill.redis.GoodsKey;
import com.smile.seckill.redis.SeckillKey;
import com.smile.seckill.redis.OrderKey;
import com.smile.seckill.redis.RedisService;
import com.smile.seckill.result.CodeMsg;
import com.smile.seckill.result.Result;
import com.smile.seckill.service.GoodsService;
import com.smile.seckill.service.SeckillService;
import com.smile.seckill.service.SeckillUserService;
import com.smile.seckill.service.OrderService;
import com.smile.seckill.vo.GoodsVo;

/**
 * 秒杀控制层
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

	@Autowired
	SeckillUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	SeckillService seckillService;
	
	@Autowired
	MQSender sender;
	
	private HashMap<Long, Boolean> localOverMap =  new HashMap<Long, Boolean>();
	
	/**
	 * 系统初始化，将秒杀商品信息写入 Redis
	 */
	public void afterPropertiesSet() {
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		if(goodsList == null) {
			return;
		}
		for(GoodsVo goods : goodsList) {
			redisService.set(GoodsKey.getSeckillGoodsStock, "" + goods.getId(), goods.getStockCount());
			localOverMap.put(goods.getId(), false);
		}
	}

    /**
     * 重置商品
     * @param model
     * @return
     */
	@RequestMapping(value="/reset", method=RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> reset(Model model) {
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		for(GoodsVo goods : goodsList) {
			goods.setStockCount(10);
			redisService.set(GoodsKey.getSeckillGoodsStock, "" + goods.getId(), 10);
			localOverMap.put(goods.getId(), false);
		}
		redisService.delete(OrderKey.getSeckillOrderByUidGid);
		redisService.delete(SeckillKey.isGoodsOver);
		seckillService.reset(goodsList);
		return Result.success(true);
	}
	
    /**
     * 秒杀
     *
     * QPS:1306
     * 5000 * 10
     * QPS: 2114
     *
     * @param model
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    @RequestMapping(value="/{path}/do_seckill", method=RequestMethod.POST)
    @ResponseBody
    public Result<Integer> seckill(Model model, SeckillUser user,
    		@RequestParam("goodsId")long goodsId,
    		@PathVariable("path") String path) {
    	model.addAttribute("user", user);
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    	//验证path
    	boolean check = seckillService.checkPath(user, goodsId, path);
    	if(!check){
    		return Result.error(CodeMsg.REQUEST_ILLEGAL);
    	}
    	//内存标记，减少redis访问
    	boolean over = localOverMap.get(goodsId);
    	if(over) {
    		return Result.error(CodeMsg.SECKILL_OVER);
    	}
    	//预减库存
    	long stock = redisService.decr(GoodsKey.getSeckillGoodsStock, "" + goodsId); //10
    	if(stock < 0) {
    		localOverMap.put(goodsId, true);
    		return Result.error(CodeMsg.SECKILL_OVER);
    	}
    	//判断是否已经秒杀到了
    	SeckillOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
    	if(order != null) {
    		return Result.error(CodeMsg.REPEATE_SECKILL);
    	}
    	//入队
		SeckillMessage seckillMessage = new SeckillMessage();
		seckillMessage.setSeckillUser(user);
		seckillMessage.setGoodsId(goodsId);
    	sender.sendSeckillMessage(seckillMessage);
    	return Result.success(0);//排队中
    	/*
    	//判断库存
    	GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);//10个商品，req1 req2
    	int stock = goods.getStockCount();
    	if(stock <= 0) {
    		return Result.error(CodeMsg.MIAO_SHA_OVER);
    	}
    	//判断是否已经秒杀到了
    	MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
    	if(order != null) {
    		return Result.error(CodeMsg.REPEATE_MIAOSHA);
    	}
    	//减库存 下订单 写入秒杀订单
    	OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
        return Result.success(orderInfo);
        */
    }
    
    /**
     * 获取秒杀结果
     *
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value="/result", method=RequestMethod.GET)
    @ResponseBody
    public Result<Long> seckillResult(Model model, SeckillUser user,
    		@RequestParam("goodsId")long goodsId) {
    	model.addAttribute("user", user);
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    	long result  = seckillService.getSeckillResult(user.getId(), goodsId);
    	return Result.success(result);
    }

    /**
     * 获取秒杀路径
     * @param request
     * @param user
     * @param goodsId
     * @param verifyCode
     * @return
     */
    @AccessLimit(seconds=5, maxCount=5, needLogin=true)
    @RequestMapping(value="/path", method=RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillPath(HttpServletRequest request, SeckillUser user,
    		@RequestParam("goodsId")long goodsId,
    		@RequestParam(value="verifyCode", defaultValue="0")int verifyCode
    		) {
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    	boolean check = seckillService.checkVerifyCode(user, goodsId, verifyCode);
    	if(!check) {
    		return Result.error(CodeMsg.REQUEST_ILLEGAL);
    	}
    	String path = seckillService.createSeckillPath(user, goodsId);
    	return Result.success(path);
    }

    /**
     * 获取验证码
     * @param response
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value="/verifyCode", method=RequestMethod.GET)
    @ResponseBody
    public Result<String> getSeckillVerifyCode(HttpServletResponse response, SeckillUser user,
    		@RequestParam("goodsId")long goodsId) {
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    	try {
    		BufferedImage image = seckillService.createVerifyCode(user, goodsId);
    		OutputStream out = response.getOutputStream();
    		ImageIO.write(image, "JPEG", out);
    		out.flush();
    		out.close();
    		return null;
    	}catch(Exception e) {
    		e.printStackTrace();
    		return Result.error(CodeMsg.SECKILL_FAIL);
    	}
    }
}