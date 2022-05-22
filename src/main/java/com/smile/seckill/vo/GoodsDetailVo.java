package com.smile.seckill.vo;

import com.smile.seckill.domain.SeckillUser;
import lombok.Data;

/**
 * 页面展示的商品详情信息实体类定义
 */
@Data
public class GoodsDetailVo {
	private int seckillStatus = 0;
	private int remainSeconds = 0;
	private GoodsVo goods ;
	private SeckillUser user;
}