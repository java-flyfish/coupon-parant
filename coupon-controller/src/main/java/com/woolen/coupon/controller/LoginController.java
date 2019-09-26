package com.woolen.coupon.controller;

import com.woolen.coupon.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Info:
 * 如代码所示，获取当前登录用户：SecurityContextHolder.getContext().getAuthentication()
 * @PreAuthorize 用于判断用户是否有指定权限，没有就不能访问
 * @ClassName: LoginController
 * @Author: weiyang
 * @Data: 2019/9/20 9:18 PM
 * @Version: V1.0
 **/
@Controller
public class LoginController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @RequestMapping("/")
    public String showHome() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("当前登陆用户：" + name);
        return "home.html";
    }

    @RequestMapping("/login")
    public String showLogin() {
        return "login.html";
    }

    @RequestMapping("/login/error")
    public void errorLogin(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        AuthenticationException exception =
                (AuthenticationException)request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        try {
            response.getWriter().write(exception.toString());
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/sms/code")
    @ResponseBody
    public void sms(String phone, HttpSession session) {

        int code = (int) Math.ceil(Math.random() * 9000 + 1000);

        //验证码10分钟有效
        redisTemplate.opsForValue().set(RedisUtils.redis_phone_code + phone,code+"",600l, TimeUnit.SECONDS);
        //todo 接发送短信
        System.out.println("短信验证码：" + code);
        logger.info("{}：为 {} 设置短信验证码：{}", session.getId(), RedisUtils.redis_phone_code + phone, code);
    }


    @GetMapping("/lind-auth/getToken")
    public String refreshAndGetAuthenticationToken(
            @RequestParam String phone,
            @RequestParam String code) throws AuthenticationException {
        return generateToken(phone, code);
    }

    /**
     * 登陆与授权.
     *
     * @param phone .
     * @param code .
     * @return
     */
    private String generateToken(String phone, String code) {
        /*SmsCodeAuthenticationToken upToken = new SmsCodeAuthenticationToken(phone);
        // Perform the security
        final Authentication authentication = authenticationManager.authenticate(upToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Reload password post-security so we can generate token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        // 持久化的redis
        String token = CommonUtils.encrypt(userDetails.getUsername());
        redisTemplate.opsForValue().set(token, userDetails.getUsername());
        return token;

        if (code == null){
            return "短信验证码未空";
        }
        String codeRedis = redisTemplate.opsForValue().get(RedisUtils.redis_phone_code + phone);
        if (code.equals(codeRedis)){
            String token = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(RedisUtils.redis_phone_token + phone,token,7l,TimeUnit.DAYS);
            System.out.println("token" + token);
            return token;
        }*/
        return "短信验证码不正确";
    }
    /*@RequestMapping("/admin")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String printAdmin() {
        return "如果你看见这句话，说明你有ROLE_ADMIN角色";
    }

    @RequestMapping("/user")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER')")
    public String printUser() {
        return "如果你看见这句话，说明你有ROLE_USER角色";
    }
*/
    @RequestMapping("/admin")
    @ResponseBody
    @PreAuthorize("hasPermission('/admin','r')")
    public String printAdminR() {
        return "如果你看见这句话，说明你访问/admin路径具有r权限";
    }

    @RequestMapping("/admin/c")
    @ResponseBody
    @PreAuthorize("hasPermission('/admin','c')")
    public String printAdminC() {
        return "如果你看见这句话，说明你访问/admin路径具有c权限";
    }
}
