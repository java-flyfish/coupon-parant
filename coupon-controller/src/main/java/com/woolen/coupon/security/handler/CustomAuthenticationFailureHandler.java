package com.woolen.coupon.security.handler;

import com.alibaba.fastjson.JSONObject;
import com.woolen.coupon.response.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Info:
 * @ClassName: CustomAuthenticationFailureHandler
 * @Author: weiyang
 * @Data: 2019/9/21 5:06 PM
 * @Version: V1.0
 **/
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        logger.info("登陆失败");
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setContentType("application/json;charset=UTF-8");
        Result result = new Result(null,false,"登陆失败！");
        response.getWriter().write(JSONObject.toJSONString(result));
        response.getWriter().flush();
    }
}
