package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.servcie.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/15 17:25
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {
            //生成4位随机验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            //调用阿里云的短信API完成发送短信
            SMSUtils.sendMessage("认证签名", "SMS_276510627", phone, code);
            //需要将生成的验证码保存到session中
            session.setAttribute(phone, code);
            log.info("code:{}",code);
            return R.success("验证码发送成功");
        }
        return R.error("验证码发送失败");
    }

    /**
     * 移动端用户登录
     *
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从session中获取保存的验证码
        Object codeInSession = session.getAttribute(phone);
        //进行验证码的比对1
        if (codeInSession != null && codeInSession.equals(code)) {
            //如果能够比对成功，登录成功

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                //判断当前手机号对应的用户是否是新用户，如果是新用户就默认完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            //将登录的信息加到session中，防止被拦截
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }
}
