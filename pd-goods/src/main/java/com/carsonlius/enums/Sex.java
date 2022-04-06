package com.carsonlius.enums;

/**
 * @Author carsonlius
 * @Date 2022/3/6 17:50
 * @Version 1.0
 */
public enum Sex {

    /**
     * W="女"
     */
    W("女"),
    /**
     * M="男"
     */
    M("男"),
    /**
     * N="未知"
     */
    N("未知"),;

    private String desc;

    Sex(String desc) {
        this.desc = desc;
    }

}
