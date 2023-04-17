package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/13 10:07
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
