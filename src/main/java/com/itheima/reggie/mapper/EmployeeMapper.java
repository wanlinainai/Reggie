package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/11 14:51
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
    //不要看没有东西，真正的东西都在mybatis-plus中。
}
