package com.smile.seckill.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smile.seckill.domain.SeckillUser;
import com.smile.seckill.domain.OrderInfo;
import com.smile.seckill.redis.RedisService;
import com.smile.seckill.result.CodeMsg;
import com.smile.seckill.result.Result;
import com.smile.seckill.service.GoodsService;
import com.smile.seckill.service.SeckillUserService;
import com.smile.seckill.service.OrderService;
import com.smile.seckill.vo.GoodsVo;
import com.smile.seckill.vo.OrderDetailVo;

/**
 * 订单控制层
 */
@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	SeckillUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	GoodsService goodsService;

	/**
	 * 订单详情
	 * @param model
	 * @param user
	 * @param orderId
	 * @return
	 */
    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model, SeckillUser user,
    		@RequestParam("orderId") long orderId) {
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    	OrderInfo order = orderService.getOrderById(orderId);
    	if(order == null) {
    		return Result.error(CodeMsg.ORDER_NOT_EXIST);
    	}
    	long goodsId = order.getGoodsId();
    	GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
    	OrderDetailVo vo = new OrderDetailVo();
    	vo.setOrder(order);
    	vo.setGoods(goods);
    	return Result.success(vo);
    }
}