package com.woolen.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.woolen.coupon.entry.User;
import com.woolen.coupon.mapper.UserMapper;
import com.woolen.coupon.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Info:
 * @ClassName: UserServiceImpl
 * @Author: weiyang
 * @Data: 2019/9/20 9:11 PM
 * @Version: V1.0
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User selectById(Integer id) {
        return userMapper.selectById(id);
    }

    @Override
    public User selectByName(String name) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("name",name);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public User selectByPhone(String phone) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("phone",phone);
        return userMapper.selectOne(wrapper);
    }
}
