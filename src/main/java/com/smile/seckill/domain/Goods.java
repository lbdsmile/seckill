package com.smile.seckill.domain;

import lombok.Data;

/**
 * 商品实体类
 */
@Data
public class Goods {
	private Long id;
	private String goodsName;
	private String goodsTitle;
	private String goodsImg;
	private String goodsDetail;
	private Double goodsPrice;
	private Integer goodsStock;
}
