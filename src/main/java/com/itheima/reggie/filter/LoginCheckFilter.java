package com.itheima.reggie.filter;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/11 17:16
 */

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 */
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配器，用于跟放行的URI请求进行比较
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1、获取本次的URI
        String requestURI = request.getRequestURI();
        log.info("请求的URI:{}", requestURI);
        //2、放行某些请求
        ///backend/**  可以匹配backend下的多级路径，但是/backend/*  只能匹配一级目录
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",//移动端发短信
                "/user/login"//
        };

        //3、判断请求是否需要处理
        boolean check = check(urls, requestURI);

        //4、不需要处理
        if (check) {
            log.info("不需要处理的请求:{}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        //5、如果已经登录就直接返回
        if (request.getSession().getAttribute("employee") != null) {
            log.info("用户已经登录:id={}", request.getSession().getAttribute("employee"));
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request, response);
            return;
        }

        if (request.getSession().getAttribute("user") != null) {
            log.info("用户已经登录:id={}", request.getSession().getAttribute("user"));
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request, response);
            return;
        }

        //6、如果没有登录就返回登录结果，通过输出流的方式向客户端相应数据
        log.info("Starting用户没有登录");
        //此处注意将R类转换成JSON格式的字符串。R类中的错误信息是NOTLOGIN。对应好request.js中的拦截器
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    public Boolean check(String[] urls, String requestUrl) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestUrl);
            if (match == true) {
                return true;
            }
        }
        return false;
    }

}
