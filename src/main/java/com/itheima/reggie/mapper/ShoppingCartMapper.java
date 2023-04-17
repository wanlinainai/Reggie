package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/16 22:23
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
