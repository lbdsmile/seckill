package com.smile.seckill.domain;

import lombok.Data;

import java.util.Date;

/**
 * 秒杀商品实体类
 */
@Data
public class SeckillGoods {
	private Long id;
	private Long goodsId;
	private Integer stockCount;
	private Date startDate;
	private Date endDate;
}
