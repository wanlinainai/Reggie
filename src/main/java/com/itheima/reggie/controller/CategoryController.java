package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.servcie.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/13 8:29
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     *
     * @param category
     * @return
     */
    @PostMapping()
    public R<String> save(@RequestBody Category category) {
        log.info("category:{}", category.toString());
        //调用Service层实现方法
        categoryService.save(category);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        //分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryMapper = new LambdaQueryWrapper<>();
        //条件,按照升序排列
        queryMapper.orderByAsc(Category::getSort);
        //查询
        categoryService.page(pageInfo, queryMapper);

        return R.success(pageInfo);
    }

    /**
     * 根据
     *
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") Long id) {
        log.info("删除分类，id:{}", id);
        categoryService.remove(id);
        return R.success("分类信息删除成功");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category) {
        log.info("修改分类信息:{}", category);
        //有什么不用 设置的 呢？updateUser、updateTime都是不需要设置的，因为在MetaObjectHandler已经对属性进行了处理。
        categoryService.updateById(category);
        return R.success("修改分类成功");
    }

    /**
     * 显示菜品分类，下拉框。  菜品管理---->菜品分类
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        //排序
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }


}
