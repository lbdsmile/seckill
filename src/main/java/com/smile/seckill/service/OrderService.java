package com.smile.seckill.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smile.seckill.dao.OrderDao;
import com.smile.seckill.domain.SeckillOrder;
import com.smile.seckill.domain.SeckillUser;
import com.smile.seckill.domain.OrderInfo;
import com.smile.seckill.redis.OrderKey;
import com.smile.seckill.redis.RedisService;
import com.smile.seckill.vo.GoodsVo;

/**
 * 订单服务
 */
@Service
public class OrderService {
	
	@Autowired
	OrderDao orderDao;
	
	@Autowired
	RedisService redisService;

    /**
     * 通过用户ID和商品ID获取秒杀订单
     * @param userId
     * @param goodsId
     * @return
     */
	public SeckillOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId) {
		//return orderDao.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
		return redisService.get(OrderKey.getSeckillOrderByUidGid, ""+userId+"_"+goodsId, SeckillOrder.class);
	}

    /**
     * 通过ID获取订单
     * @param orderId
     * @return
     */
	public OrderInfo getOrderById(long orderId) {
		return orderDao.getOrderById(orderId);
	}

    /**
     * 创建订单
     * @param user
     * @param goods
     * @return
     */
	@Transactional
	public OrderInfo createOrder(SeckillUser user, GoodsVo goods) {
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setCreateDate(new Date());
		orderInfo.setDeliveryAddrId(0L);
		orderInfo.setGoodsCount(1);
		orderInfo.setGoodsId(goods.getId());
		orderInfo.setGoodsName(goods.getGoodsName());
		orderInfo.setGoodsPrice(goods.getSeckillPrice());
		orderInfo.setOrderChannel(1);
		orderInfo.setStatus(0);
		orderInfo.setUserId(user.getId());
		orderDao.insert(orderInfo);
		SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId(user.getId());
		orderDao.insertSeckillOrder(seckillOrder);
		redisService.set(OrderKey.getSeckillOrderByUidGid, ""+user.getId() + "_" + goods.getId(), seckillOrder);
		return orderInfo;
	}

    /**
     * 删除订单
     */
	public void deleteOrders() {
		orderDao.deleteOrders();
		orderDao.deleteSeckillOrders();
	}

}