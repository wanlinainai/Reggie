package com.itheima.reggie.servcie.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.mapper.OrderDetailMapper;
import com.itheima.reggie.servcie.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/17 16:43
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
