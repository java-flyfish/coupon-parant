package com.woolen.coupon.service;

import com.woolen.coupon.entry.User;

import java.util.Map;

/**
 * @Info:
 * @ClassName: UserService
 * @Author: weiyang
 * @Data: 2019/9/20 9:10 PM
 * @Version: V1.0
 **/
public interface UserService {
    User selectById(Integer id);
    User selectByName(String name);
    User selectByPhone(String phone);

    Boolean insertUserAndRole(Map<String, Object> paramMap);
}
