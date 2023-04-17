package com.itheima.reggie.servcie;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Orders;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/17 16:41
 */
public interface OrderService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);
}
