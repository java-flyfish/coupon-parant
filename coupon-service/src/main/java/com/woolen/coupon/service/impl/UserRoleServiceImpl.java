package com.woolen.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.woolen.coupon.entry.UserRole;
import com.woolen.coupon.mapper.UserRoleMapper;
import com.woolen.coupon.service.UserRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Info:
 * @ClassName: UserRoleServiceImpl
 * @Author: weiyang
 * @Data: 2019/9/20 9:25 PM
 * @Version: V1.0
 **/
@Service
public class UserRoleServiceImpl implements UserRoleService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public List<UserRole> listByUserId(Integer userId) {
        QueryWrapper<UserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId);
        return userRoleMapper.selectList(wrapper);
    }
}
