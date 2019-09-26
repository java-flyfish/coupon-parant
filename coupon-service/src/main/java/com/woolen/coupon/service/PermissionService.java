package com.woolen.coupon.service;

import com.woolen.coupon.entry.Permission;

import java.util.List;

/**
 * @Info:
 * @ClassName: PermissionService
 * @Author: weiyang
 * @Data: 2019/9/21 1:57 PM
 * @Version: V1.0
 **/
public interface PermissionService {
    List<Permission> listByRoleId(Integer roleId);
}
