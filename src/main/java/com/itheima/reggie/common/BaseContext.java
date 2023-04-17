package com.itheima.reggie.common;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/12 22:07
 */
public class BaseContext {
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<Long>();
    /**
     * 设置id值
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 获取id值
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
