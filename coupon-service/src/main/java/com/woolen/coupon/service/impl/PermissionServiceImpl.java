package com.woolen.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.woolen.coupon.entry.Permission;
import com.woolen.coupon.mapper.PermissionMapper;
import com.woolen.coupon.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Info:
 * @ClassName: PermissionServiceImpl
 * @Author: weiyang
 * @Data: 2019/9/21 1:58 PM
 * @Version: V1.0
 **/
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<Permission> listByRoleId(Integer roleId) {
        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        wrapper.eq("role_id",roleId);
        return permissionMapper.selectList(wrapper);
    }
}
