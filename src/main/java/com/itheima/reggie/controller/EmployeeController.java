package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.servcie.EmployeeServcie;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/11 14:57
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeServcie employeeServcie;

    /**
     * 员工登录
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        /**
         * 将页面提交的密码加密成md5加密
         */
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //根据页面提交的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeServcie.getOne(queryWrapper);

        //如果查询不到
        if (emp == null) {
            return R.error("登录失败");
        }
        //密码对比，如果密码对比不成功，则失败
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }
        //查看员工状态，如果已经禁用的话，就显示已经禁用
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }
        //登录成功
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("已退出");
    }

    /**
     * 新增员工
     *
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息:{}", employee.toString());
        //我们给它设置一个默认的密码，假设是123456，之后员工拥有账号便可以自行更改密码。
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        //获得当前登录的用户id
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        employeeServcie.save(employee);
        return R.success("添加成功");
    }

    /**
     * 员工信息分页查询，基于mybatis-plus插件
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page = {},pageSize = {},name = {}", page, pageSize, name);

        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加一个过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeServcie.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 更新用户的数据，包括status状态等等
     *
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info(employee.toString());
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        //需要获取当前更改的时间和更改人
//        employee.setUpdateUser(empId);
//        employee.setUpdateTime(LocalDateTime.now());
        long id = Thread.currentThread().getId();
        log.info("线程id:{}",id);
        employeeServcie.updateById(employee);
        return R.success("员工信息更改成功");
    }

    /**
     * 根据id查询员工
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable("id") Long id) {
        log.info("根据id查询员工信息");
        Employee employee = employeeServcie.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("没有查询到员工信息");
    }

}