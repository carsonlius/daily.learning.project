package com.carsonlius.services.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.carsonlius.entity.User;
import com.carsonlius.mapper.UserMapper;
import com.carsonlius.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author carsonlius
 * @Date 2022/3/6 17:54
 * @Version 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public List<User> lists() {
        List<User> userList = this.list();
        System.out.println("用户列表"+ userList);

        return userList;
    }
}
