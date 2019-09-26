package com.woolen.coupon.security.handler;

import com.woolen.coupon.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Info:
 * @ClassName: CustomAuthenticationSuccessHandler
 * @Author: weiyang
 * @Data: 2019/9/21 5:05 PM
 * @Version: V1.0
 **/
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.info("登录成功,{}", authentication);
        //登陆成功信息使用response写回去
        response.setContentType("application/json;charset=UTF-8");
//        String token = UUID.randomUUID().toString();
//        redisTemplate.opsForValue().set(RedisUtils.redis_phone_token + token,authentication.getName(),7, TimeUnit.DAYS);
//        System.out.println("token:" + token);
        response.getWriter().write("登录成功");
//        response.sendRedirect("/");
    }
}
