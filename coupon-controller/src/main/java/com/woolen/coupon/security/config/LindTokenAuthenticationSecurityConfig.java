package com.woolen.coupon.security.config;

import com.woolen.coupon.security.filter.LindTokenAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

/**
 * @Info:
 * @ClassName: SmsCodeAuthenticationSecurityConfig
 * @Author: weiyang
 * @Data: 2019/9/22 7:29 AM
 * @Version: V1.0
 **/
@Component
public class LindTokenAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private LindTokenAuthenticationFilter lindTokenAuthenticationFilter;

    @Override
    public void configure(HttpSecurity http) throws Exception {

        http.addFilterAfter(lindTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
