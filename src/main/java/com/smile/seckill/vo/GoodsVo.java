package com.smile.seckill.vo;

import com.smile.seckill.domain.Goods;

import java.util.Date;

/**
 * 页面展示的商品信息实体类定义
 */
public class GoodsVo extends Goods {
	private Double seckillPrice;
	private Integer stockCount;
	private Date startDate;
	private Date endDate;
}