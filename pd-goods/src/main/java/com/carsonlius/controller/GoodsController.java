package com.carsonlius.controller;

import com.carsonlius.dto.BaseDto;
import com.carsonlius.entity.GoodsInfo;
import com.carsonlius.services.GoodsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @version V1.0
 * @author: liusen
 * @date: 2022年02月25日 15时55分
 * @contact
 * @company
 */
@RestController
@RequestMapping("/goods")
@Api(value = "商品模块", tags = "商品模块")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "merchantId", value = "", dataType = "String", paramType = "query", required= true)
    })
    @ApiOperation(value = "获取全量商品列表", httpMethod = "GET", response = List.class)
    public List<GoodsInfo> goods(BaseDto baseDto){
        return goodsService.getGoods();
    }
}
