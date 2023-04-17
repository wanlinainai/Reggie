package com.itheima.reggie.servcie.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.mapper.ShoppingCartMapper;
import com.itheima.reggie.servcie.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/16 22:26
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
