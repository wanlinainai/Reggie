package com.itheima.reggie.servcie.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.servcie.CategoryService;
import com.itheima.reggie.servcie.DishService;
import com.itheima.reggie.servcie.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/13 8:24
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除分类，删除之前要进行判断
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);

        //查询当前分类是否关联菜菜品，如果一斤关联，抛出一个业务异常
        if (count1 > 0) {
            //已经关联，抛出一个异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        //查询当前分类是否关联套餐，如果一斤关联，抛出一个业务异常
        if (count2 > 0) {
            //已经关联，抛出异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        //正常删除
        super.removeById(id);
    }
}
