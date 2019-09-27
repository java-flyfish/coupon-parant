package com.woolen.coupon.security.provider;

import com.woolen.coupon.security.token.SmsCodeAuthenticationToken;
import com.woolen.coupon.utils.RedisUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @Info:
 * @ClassName: SmsAuthenticationProvider
 * @Author: weiyang
 * @Data: 2019/9/22 7:20 AM
 * @Version: V1.0
 **/
public class SmsCodeAuthenticationProvider implements AuthenticationProvider {
    private UserDetailsService userDetailsService;
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;

        String phone = (String) authenticationToken.getPrincipal();
        String code = (String) authenticationToken.getCredentials();

        checkSmsCode(phone,code);

        UserDetails userDetails = userDetailsService.loadUserByUsername(phone);

        // 此时鉴权成功后，应当重新 new 一个拥有鉴权的 authenticationResult 返回
        SmsCodeAuthenticationToken authenticationResult = new SmsCodeAuthenticationToken(userDetails,phone, userDetails.getAuthorities());

        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;
    }

    private void checkSmsCode(String phone,String code) {
        String smsCode = redisTemplate.opsForValue().get(RedisUtils.redis_phone_code + phone);

        if(smsCode == null) {
            throw new BadCredentialsException("未检测到申请验证码");
        }

        if(!code.equals(smsCode)) {
            throw new BadCredentialsException("验证码错误");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 判断 authentication 是不是 SmsCodeAuthenticationToken 的子类或子接口
        return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public RedisTemplate<String, String> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
