package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrderDto;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.servcie.OrderDetailService;
import com.itheima.reggie.servcie.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/17 16:44
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        orderService.submit(orders);
        return R.success("添加成功");
    }

    /**
     * 获取到订单细节，为了防止出现嵌套
     *
     * @return
     */
    public List<OrderDetail> getOrderDetailList(Long orderId) {
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId, orderId);
        List<OrderDetail> orderList = orderDetailService.list(queryWrapper);
        return orderList;
    }

    /**
     * 完整分页查询,由于没有部分属性，我们需要利用Dto来创建出这些属性
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> page(int page, int pageSize) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrderDto> pageDto = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        wrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo, wrapper);
        List<Orders> records = pageInfo.getRecords();
        List<OrderDto> orderDtos = records.stream().map((item) -> {
            OrderDto orderDto = new OrderDto();
            //此时的orderDto还是空的
            //此时是将每一条orders中的数据赋值给orderDto，是上边新建的orderDto。
            BeanUtils.copyProperties(item, orderDto);
            //缺少口味等
            //获取订单id
            Long orderId = item.getId();
            //根据订单id查询细节
            List<OrderDetail> orderDetailList = this.getOrderDetailList(orderId);
            orderDto.setOrderDetailList(orderDetailList);
            return orderDto;
        }).collect(Collectors.toList());
        //除了records列不用复制，其他的复制给pageDto页
        BeanUtils.copyProperties(pageInfo,pageDto,"records");
        pageDto.setRecords(orderDtos);
        return R.success(pageDto);
    }


}
