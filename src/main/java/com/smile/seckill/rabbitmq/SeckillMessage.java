package com.smile.seckill.rabbitmq;

import com.smile.seckill.domain.SeckillUser;
import lombok.Data;

@Data
public class SeckillMessage {
	private SeckillUser seckillUser;
	private long goodsId;
}
