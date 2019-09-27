package com.woolen.coupon.security.evaluator;

import com.woolen.coupon.entry.Permission;
import com.woolen.coupon.service.PermissionService;
import com.woolen.coupon.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @Info: 授权后用户的角色管理器
 * @ClassName: CustomPermissionEvaluator
 * @Author: weiyang
 * @Data: 2019/9/21 2:06 PM
 * @Version: V1.0
 **/
@Component//已经在设置这个角色管理器的时候new了，但是这个注解是必须的，不知道什么原因
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetUrl, Object targetPermission) {
        // 获得loadUserByUsername()方法的结果,这是security中的user类
        User user = (User)authentication.getPrincipal();
        // 获得loadUserByUsername()中注入的角色
        Collection<GrantedAuthority> authorities = user.getAuthorities();

        // 遍历用户所有角色
        for(GrantedAuthority authority : authorities) {
            String roleName = authority.getAuthority();
            Integer roleId = roleService.selectByName(roleName).getId();
            // 得到角色所有的权限
            List<Permission> permissionList = permissionService.listByRoleId(roleId);

            // 遍历permissionList
            for (Permission permission : permissionList) {
                // 获取权限集
                List<String> permissions = Arrays.asList(permission.getPermission().split(","));
                // 如果访问的Url和权限用户符合的话，返回true
                if (targetUrl.equals(permission.getUrl())
                        && permissions.contains(targetPermission)) {
                    return true;
                }
            }

        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }
}
