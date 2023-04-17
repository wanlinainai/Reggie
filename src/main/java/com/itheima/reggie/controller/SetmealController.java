package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.servcie.CategoryService;
import com.itheima.reggie.servcie.SetmealDishServcie;
import com.itheima.reggie.servcie.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/14 20:22
 */

/**
 * 套餐管理
 */
@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealDishServcie setmealDishServcie;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("Setmeal:{}", setmealDto.toString());

        setmealService.saveWithDish(setmealDto);
        return R.success("添加套餐成功");
    }

    /**
     * 回显列表
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.like(name != null, Setmeal::getName, name);
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage, setmealLambdaQueryWrapper);

        //获取Setmeal的所有数据
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> list =
                records.stream().map((item) -> {
                    SetmealDto setmealDto = new SetmealDto();
                    BeanUtils.copyProperties(item, setmealDto);
                    Long categoryId = item.getCategoryId();
                    Category category = categoryService.getById(categoryId);
                    if (category != null) {
                        String categoryName = category.getName();
                        setmealDto.setCategoryName(categoryName);
                    }
                    return setmealDto;
                }).collect(Collectors.toList());

        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("ids:{}",ids);

        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }

    /**
     * 套餐的启售停售
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable("status") Integer status, @RequestParam List<Long> ids) {
        log.info("ids:{}",ids);
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 先查找，把每一个应该处理的值拿出来
        setmealLambdaQueryWrapper.in(ids != null, Setmeal::getId,ids);
        List<Setmeal> lists = setmealService.list(setmealLambdaQueryWrapper);
        for (Setmeal list : lists) {
            if (list != null) {
                list.setStatus(status);
                setmealService.updateById(list);
            }
        }
        return R.success("状态修改成功");
    }

    /**
     * 套餐回显
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable() Long id) {
        log.info("id:{}",id);
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    @PutMapping()
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        log.info("setmelaDto:{}",setmealDto);
        setmealService.updateWithDish(setmealDto);
        return R.success("更新成功");
    }

    /**
     * 通过健值对的形式不需要处理JSON格式，@RequestBody
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> get(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime) ;
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }


}
