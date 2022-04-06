package com.carsonlius.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 基参
 * @Author carsonlius
 * @Date 2022/3/12 15:38
 * @Version 1.0
 */
@Data
public class BaseDto {
    /**
     * 租户ID
     * */
    @ApiModelProperty(value = "租户ID")
    public String merchantId;
}
