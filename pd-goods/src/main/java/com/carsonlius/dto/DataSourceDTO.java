package com.carsonlius.dto;

import lombok.Data;

/**
 * @version V1.0
 * @author: liusen
 * @date: 2022年03月08日 19时36分
 * @contact
 * @company
 */
@Data
public class DataSourceDTO {

    /**
     * 连接池名称
     */
    private String poolName;

    /**
     * JDBC driver org.h2.Driver
     */
    private String driverClassName;

    /**
     * JDBC url 地址
     */
    private String url;

    /**
     * JDBC 用户名
     */
    private String username;

    /**
     * JDBC 密码
     */
    private String password;
}
