package com.smile.seckill.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smile.seckill.dao.GoodsDao;
import com.smile.seckill.domain.SeckillGoods;
import com.smile.seckill.vo.GoodsVo;

/**
 * 商品服务
 */
@Service
public class GoodsService {
	
	@Autowired
	GoodsDao goodsDao;

    /**
     * 返回商品列表信息
     * @return
     */
	public List<GoodsVo> listGoodsVo(){
		return goodsDao.listGoodsVo();
	}

    /**
     * 通过商品id获取商品信息
     * @param goodsId
     * @return
     */
	public GoodsVo getGoodsVoByGoodsId(long goodsId) {
		return goodsDao.getGoodsVoByGoodsId(goodsId);
	}

    /**
     * 减库存
     * @param goods
     * @return
     */
	public boolean reduceStock(GoodsVo goods) {
		SeckillGoods seckillGoods = new SeckillGoods();
        seckillGoods.setGoodsId(goods.getId());
		int ret = goodsDao.reduceStock(seckillGoods);
		return ret > 0;
	}

    /**
     * 重置库存
     * @param goodsList
     */
	public void resetStock(List<GoodsVo> goodsList) {
		for(GoodsVo goodsVo : goodsList ) {
			SeckillGoods seckillGoods = new SeckillGoods();
			seckillGoods.setGoodsId(goodsVo.getId());
			seckillGoods.setStockCount(goodsVo.getStockCount());
			goodsDao.resetStock(seckillGoods);
		}
	}
}