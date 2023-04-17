package com.itheima.reggie.servcie;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

import java.util.List;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/13 10:09
 */
public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应 的口味数据 ，需要操作两张表：Dish、Dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    // 查询id对应的基本信息和口味信息
    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);

    //删除
    public void deleteByIds(List<Long> ids);
}
