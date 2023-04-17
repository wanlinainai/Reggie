package com.itheima.reggie.servcie.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.mapper.UserMapper;
import com.itheima.reggie.servcie.UserService;
import org.springframework.stereotype.Service;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/15 17:24
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
