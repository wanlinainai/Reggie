package com.itheima.reggie.servcie;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/13 8:24
 */

public interface CategoryService extends IService<Category> {
     void remove(Long id);
}
