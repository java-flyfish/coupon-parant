package com.woolen.coupon.service;

import com.woolen.coupon.entry.Role;

/**
 * @Info:
 * @ClassName: UserService
 * @Author: weiyang
 * @Data: 2019/9/20 9:10 PM
 * @Version: V1.0
 **/
public interface RoleService {
    Role selectById(Integer id);
    Role selectByName(String name);
}
