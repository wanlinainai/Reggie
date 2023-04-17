package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/17 16:38
 */
@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
