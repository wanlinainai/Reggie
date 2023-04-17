package com.itheima.reggie.servcie.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealDishMapper;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.servcie.SetmealDishServcie;
import com.itheima.reggie.servcie.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/13 10:11
 */
@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishServcie setmealDishServcie;

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增套餐，同时需要保证套餐和菜品关系的关联
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息
        //选择字段，策略插入
        this.save(setmealDto);

        List<SetmealDish> list = setmealDto.getSetmealDishes();
        list.stream().map((item) ->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //保存套餐和菜品的关联关系
        setmealDishServcie.saveBatch(list);
    }

    /**
     * 删除套餐并且将菜品也删除
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //select count(*) from setmeal in (dis[0],ids[1],ids[2]) where status = 1
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.in(Setmeal::getId,ids);
        setmealQueryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(setmealQueryWrapper);
        if (count > 0) {
            //这一堆里边有的是售卖状态，不能删除
            throw new CustomException("有商品正在售卖，不能删除");
        }
        //可以删除的话，先删除套餐表中的数据
        //setmealMapper.delete(setmealQueryWrapper);
        this.removeByIds(ids);
        //在删除关系表中的数据,由于ids不是Setmeal_dish的主键
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //delete from setmeal_dish where setmeal_id in (ids[0],ids[1],ids[2]...)
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        //setmealDishMapper.delete(setmealDishLambdaQueryWrapper);
        setmealDishServcie.remove(setmealDishLambdaQueryWrapper);
    }

    /**
     * 回显套餐数据
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();

        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(id != null,SetmealDish::getSetmealId,id);
        if (setmeal != null){
            BeanUtils.copyProperties(setmeal, setmealDto);
            List<SetmealDish> list = setmealDishServcie.list(setmealDishLambdaQueryWrapper);
            setmealDto.setSetmealDishes(list);
            return setmealDto;
        }
        return null;
    }

    @Transactional
    public void updateWithDish(SetmealDto setmealDto){
        this.updateById(setmealDto);
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishServcie.remove(setmealDishLambdaQueryWrapper);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) ->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishServcie.saveBatch(setmealDishes);
    }
}
