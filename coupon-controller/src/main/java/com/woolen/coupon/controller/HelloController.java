package com.woolen.coupon.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.woolen.coupon.entry.User;
import com.woolen.coupon.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Info:
 * @ClassName: HelloController
 * @Author: weiyang
 * @Data: 2019/9/18 2:33 PM
 * @Version: V1.0
 **/
@RestController
@RequestMapping("/hello")
public class HelloController {

    @Autowired
    UserMapper userMapper;

    @GetMapping("/hello")
    public String hello(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("id",1);

        User user = userMapper.selectOne(wrapper);
        return "hello";
    }
}
