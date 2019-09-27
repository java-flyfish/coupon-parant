package com.woolen.coupon.security.handler;

import com.alibaba.fastjson.JSONObject;
import com.woolen.coupon.response.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Info: 用户无权访问的处理
 * @ClassName: CustomAccessDeniedHandler
 * @Author: weiyang
 * @Data: 2019/9/27 10:49 AM
 * @Version: V1.0
 **/
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        //返回json形式的错误信息
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        Result result = new Result(null,false,"您无权访问该接口！我愚蠢的弟弟啊 ! 你跑错厕所了 !");
        response.getWriter().println(JSONObject.toJSONString(result));
        response.getWriter().flush();
    }
}
