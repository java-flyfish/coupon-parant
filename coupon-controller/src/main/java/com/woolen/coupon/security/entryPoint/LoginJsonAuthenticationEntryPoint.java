package com.woolen.coupon.security.entryPoint;

import com.alibaba.fastjson.JSONObject;
import com.woolen.coupon.response.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Info:
 * @ClassName: LoginJsonAuthenticationEntryPoint
 * @Author: weiyang
 * @Data: 2019/9/25 1:57 PM
 * @Version: V1.0
 **/
public class LoginJsonAuthenticationEntryPoint implements AuthenticationEntryPoint{

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // redirect to login page. Use https if forceHttps true
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();

        Result result = new Result(null,false,"请登录！");

        out.write(JSONObject.toJSONString(result));
        out.flush();
        out.close();
    }
}
