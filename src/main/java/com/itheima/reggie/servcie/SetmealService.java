package com.itheima.reggie.servcie;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/13 10:09
 */
public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐同时删除菜品
     *
     * @param ids
     */
    public void removeWithDish(List<Long> ids);

    /**
     * 修改时候的回显操作
     * @param id
     * @return
     */
    public SetmealDto getByIdWithDish(Long id);

    void updateWithDish(SetmealDto setmealDto);
}
