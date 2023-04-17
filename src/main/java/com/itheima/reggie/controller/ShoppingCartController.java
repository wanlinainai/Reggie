package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.servcie.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/16 22:27
 */
@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("shoppingCart:{}", shoppingCart);
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> query = new LambdaQueryWrapper<>();
        query.eq(ShoppingCart::getUserId, shoppingCart.getUserId());
        //如果菜品的id不为null，那么添加的就是菜品
        if (dishId != null) {
            //添加条件
            query.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            query.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        //得到查询的菜品或者套餐
        ShoppingCart shoppingCartIncludes = shoppingCartService.getOne(query);
        BigDecimal amount = shoppingCart.getAmount();
        if (shoppingCartIncludes != null) {
            Integer number = shoppingCartIncludes.getNumber();
            shoppingCartIncludes.setNumber(number + 1);
//            shoppingCartIncludes.setAmount(shoppingCartIncludes.getAmount().add(amount));
            shoppingCartService.updateById(shoppingCartIncludes);
        } else {
            //查询到的是null，就添加，设置number默认是1。
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            shoppingCartIncludes = shoppingCart;
        }

        return R.success(shoppingCartIncludes);
    }


    /**
     * 查看购物车
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        log.info("查看购物车...");
        //注意用户的概念，每个用户只能看自己的购物车信息
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);

        return R.success("清空成功...");
    }

    /**
     * 减少菜品或者套餐的数量
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    @Transactional
    public R<String> sub(@RequestBody ShoppingCart shoppingCart) {
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //是菜品
        if (dishId != null) {
            //由于我们查找的是不同用户，所以说一点要处理好用户id请求。
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
            queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

            ShoppingCart cart1 = shoppingCartService.getOne(queryWrapper);
            //找到某一个用户的购物车之后
            cart1.setNumber(cart1.getNumber() - 1);
            Integer number = cart1.getNumber();
            if (number > 0) {
                //还有餐品
                shoppingCartService.updateById(cart1);
            } else if (number == 0) {
                shoppingCartService.removeById(cart1.getId());
            } else {
                //报错了
                return R.error("出错了");
            }
            return R.success("cart1");
        } else {
            //是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
            queryWrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());
            ShoppingCart cart2 = shoppingCartService.getOne(queryWrapper);
            cart2.setNumber(cart2.getNumber() - 1);
            Integer number = cart2.getNumber();
            if (number > 0) {
                //还有套餐
                shoppingCartService.updateById(cart2);
            }else if (number == 0) {
                shoppingCartService.removeById(cart2.getId());
            }else{
                return R.error("出错了");
            }
            return R.success("cart2");
        }
    }


}
