package com.carsonlius.services;

import com.baomidou.mybatisplus.extension.service.IService;
import com.carsonlius.entity.User;

import java.util.List;

/**
 * @Author carsonlius
 * @Date 2022/3/6 17:54
 * @Version 1.0
 */
public interface UserService extends IService<User> {
    List<User> lists();
}
