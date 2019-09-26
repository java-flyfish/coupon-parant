package com.woolen.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.woolen.coupon.entry.Role;
import com.woolen.coupon.mapper.RoleMapper;
import com.woolen.coupon.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Info:
 * @ClassName: RoleServiceImpl
 * @Author: weiyang
 * @Data: 2019/9/20 9:13 PM
 * @Version: V1.0
 **/
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Role selectById(Integer id) {
        return roleMapper.selectById(id);
    }

    @Override
    public Role selectByName(String name) {
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        wrapper.eq("name",name);
        return roleMapper.selectOne(wrapper);
    }
}
