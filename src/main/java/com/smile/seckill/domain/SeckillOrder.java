package com.smile.seckill.domain;

import lombok.Data;

/**
 * 秒杀订单实体类
 */
@Data
public class SeckillOrder {
	private Long id;
	private Long userId;
	private Long  orderId;
	private Long goodsId;
}
