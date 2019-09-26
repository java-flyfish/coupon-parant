package com.woolen.coupon.service;

import com.woolen.coupon.entry.UserRole;

import java.util.List;

/**
 * @Info:
 * @ClassName: UserRoleService
 * @Author: weiyang
 * @Data: 2019/9/20 9:25 PM
 * @Version: V1.0
 **/
public interface UserRoleService {
    List<UserRole> listByUserId(Integer userId);
}
