package com.itheima.reggie.common;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/11 21:24
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常捕获
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobleExceptionHandler {

    /**
     * 异常处理方法
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error(ex.getMessage());
        //如果出现Duplicate entry关键词
        if (ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("鸡你太美");
    }

    /**
     * 自定义异常类的全局处理方法
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex) {
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }

    /**
     * 处理没有图片的错误
     */
    @ExceptionHandler(FileNotFoundException.class)
    public R<String> exceptionHandler(FileNotFoundException ex) {
        log.info(ex.getMessage());
        return R.error("图片不存在");
    }

}
