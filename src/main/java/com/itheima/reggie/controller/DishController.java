package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.servcie.CategoryService;
import com.itheima.reggie.servcie.DishFlavorService;
import com.itheima.reggie.servcie.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/13 17:51
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //判断条件
        queryWrapper.like(name != null, Dish::getName, name);
        //排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(dishPage, queryWrapper);
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");
        List<Dish> records = dishPage.getRecords();
        //将实体类dto进行setName设置值
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);
            String image = item.getImage();
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
//        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable("id") Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R.success("更新成功");
    }

    /**
     * 启售停售操作
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable("status") Integer status, @RequestParam List<Long> ids) {
        log.info("status:{}", status);
        log.info("ids:{}", ids);
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(ids != null, Dish::getId, ids);
        List<Dish> list = dishService.list(dishLambdaQueryWrapper);
        for (Dish dish : list) {
            if (dish != null) {
                dish.setStatus(status);
                dishService.updateById(dish);
            }
        }
        return R.success("操作成功");
    }

    /**
     * 批量删除和单个删除，因为走的请求是一样的
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") List<Long> ids) {
        //此处只是删除了dish表中的数据
        dishService.deleteByIds(ids);
        //删除dish_flavor表中的口味数据
        LambdaQueryWrapper<DishFlavor> dishFlavor = new LambdaQueryWrapper<>();
        dishFlavor.in(ids != null, DishFlavor::getDishId, ids);
        dishFlavorService.remove(dishFlavor);
        return R.success("删除成功");
    }

    /**
     * 在套餐菜品中添加菜品，根据对应的类型id查找出该分类下的所有的菜品
     *
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish) {
//        //构造查询条件
//        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        dishLambdaQueryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
//        dishLambdaQueryWrapper.eq(Dish::getStatus, 1);
//        //排序
//        dishLambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(dishLambdaQueryWrapper);
//        return R.success(list);
//    }
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        //构造查询条件
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        dishLambdaQueryWrapper.eq(Dish::getStatus, 1);
        //排序
        dishLambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(dishLambdaQueryWrapper);
        List<DishDto> dishDtoList =
                list.stream().map((item) -> {
                    DishDto dishDto = new DishDto();
                    BeanUtils.copyProperties(item, dishDto);
                    //获取口味属性
                    LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                    lambdaQueryWrapper.eq(DishFlavor::getDishId, item.getId());
                    List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
                    dishDto.setFlavors(dishFlavorList);
                    return dishDto;
                }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }
}
