package com.itheima.reggie.common;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/13 10:32
 */

/**
 * 自定义异常类
 */
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
