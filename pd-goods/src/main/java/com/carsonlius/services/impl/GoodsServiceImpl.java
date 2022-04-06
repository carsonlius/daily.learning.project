package com.carsonlius.services.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.carsonlius.entity.GoodsInfo;
import com.carsonlius.mapper.GoodsInfoMapper;
import com.carsonlius.services.GoodsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @version V1.0
 * @author: liusen
 * @date: 2022年02月25日 15时52分
 * @contact
 * @company
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsInfoMapper, GoodsInfo> implements GoodsService {

    @Override
    public List<GoodsInfo> getGoods(){

        return this.list();
    }
}
