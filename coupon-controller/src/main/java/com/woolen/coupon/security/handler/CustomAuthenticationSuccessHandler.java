package com.woolen.coupon.security.handler;

import com.alibaba.fastjson.JSONObject;
import com.woolen.coupon.entry.User;
import com.woolen.coupon.entry.UvStatistic;
import com.woolen.coupon.response.Result;
import com.woolen.coupon.response.UserVo;
import com.woolen.coupon.service.UserService;
import com.woolen.coupon.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
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

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.info("登录成功,{}", authentication);
        //登陆成功信息使用response写回去
        response.setContentType("application/json;charset=UTF-8");
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(RedisUtils.redis_phone_token + token,authentication.getName(),7, TimeUnit.DAYS);
        User user = userService.selectByPhone(authentication.getName());
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user,userVo);
        userVo.setToken(token);
        System.out.println("token:" + token);
        Result result = new Result(userVo,true,"登陆成功！");

        UvStatistic uvStatistic = new UvStatistic();
        String osType = request.getHeader("osType");
        uvStatistic.setSource(Integer.valueOf(osType));
        uvStatistic.setType(1);//登陆
        uvStatistic.setUserId(user.getId());
        sendRedisMsg(uvStatistic,user.getPhone());
        PrintWriter writer = response.getWriter();
        writer.write(JSONObject.toJSONString(result));
        writer.flush();
        writer.close();
    }

    public void sendRedisMsg(UvStatistic uvStatistic, String phone){
        if (!redisTemplate.hasKey(RedisUtils.redis_uv_prefix + phone)){
            redisTemplate.convertAndSend(RedisUtils.redis_uv_topic, JSONObject.toJSONString(uvStatistic));
            redisTemplate.opsForValue().set(RedisUtils.redis_uv_prefix + phone,"1");
        }
    }
}
