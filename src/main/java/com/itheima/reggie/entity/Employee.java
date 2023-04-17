package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/11 14:41
 */
@Data
public class Employee implements Serializable {
    private Long id;

    private String name;

    private String username;

    private String password;

    private String phone;

    private String sex;
    //驼峰命名
    private String idNumber;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT)
    private Long updateUser;

}
