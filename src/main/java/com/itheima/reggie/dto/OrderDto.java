package com.itheima.reggie.dto;

import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import lombok.Data;

import java.util.List;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/17 20:15
 */
@Data
public class OrderDto extends Orders {
    private List<OrderDetail> orderDetailList;
}
