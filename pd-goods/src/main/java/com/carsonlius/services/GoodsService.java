package com.carsonlius.services;

import com.baomidou.mybatisplus.extension.service.IService;
import com.carsonlius.entity.GoodsInfo;

import java.util.List;

/**
 * @version V1.0
 * @author: liusen
 * @date: 2022年02月25日 15时50分
 * @contact
 * @company
 */
public interface GoodsService  extends IService<GoodsInfo> {

     List<GoodsInfo> getGoods();
}
