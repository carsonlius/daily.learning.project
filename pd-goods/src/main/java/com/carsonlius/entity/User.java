package com.carsonlius.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.carsonlius.enums.Sex;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;

/**
 * @Author carsonlius
 * @Date 2022/3/6 17:49
 * @Version 1.0
 */
@TableName("pd_auth_user")
@Data
@ApiModel(value = "用户信息")
public class User {

    private static final long serialVersionUID = 1L;

    @TableField
    @ApiModelProperty("ID")
    private Long id;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号")
    @TableField(value = "account", condition = LIKE)
    private String account;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    @TableField(value = "name", condition = LIKE)
    private String name;

    /**
     * 组织ID
     * #pd_core_org
     */
    @TableField("org_id")
    private Long orgId;

    /**
     * 岗位ID
     * #pd_core_station
     */
    @TableField("station_id")
    private Long stationId;

    /**
     * 邮箱
     */
    @TableField(value = "email", condition = LIKE)
    private String email;

    /**
     * 手机
     */
    @TableField(value = "mobile", condition = LIKE)
    private String mobile;

    /**
     * 性别
     * #Sex{W:女;M:男;N:未知}
     */
    @TableField("sex")
    private Sex sex;

    /**
     * 启用状态 1启用 0禁用
     */
    @TableField("status")
    private Boolean status;

    /**
     * 头像
     */
    @TableField(value = "avatar", condition = LIKE)
    private String avatar;

    /**
     * 工作描述
     * 比如：  市长、管理员、局长等等   用于登陆展示
     */
    @TableField(value = "work_describe", condition = LIKE)
    private String workDescribe;

    /**
     * 最后一次输错密码时间
     */
    @TableField("password_error_last_time")
    private LocalDateTime passwordErrorLastTime;

    /**
     * 密码错误次数
     */
    @TableField("password_error_num")
    private Integer passwordErrorNum;

    /**
     * 密码过期时间
     */
    @TableField("password_expire_time")
    private LocalDateTime passwordExpireTime;

    /**
     * 密码
     */
    @TableField(value = "password", condition = LIKE)
    private String password;

    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

}
