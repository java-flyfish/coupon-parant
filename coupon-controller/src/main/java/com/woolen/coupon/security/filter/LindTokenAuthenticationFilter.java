package com.woolen.coupon.security.filter;

import com.alibaba.fastjson.JSONObject;
import com.woolen.coupon.entry.User;
import com.woolen.coupon.entry.UvStatistic;
import com.woolen.coupon.security.token.LindTokenAuthenticationToken;
import com.woolen.coupon.service.UserService;
import com.woolen.coupon.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Info:
 * @ClassName: LindTokenAuthenticationFilter
 * @Author: weiyang
 * @Data: 2019/9/23 9:09 PM
 * @Version: V1.0
 **/
@Component
public class LindTokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    String tokenHead = "Bearer ";
    String tokenHeader = "Authorization";

    @Autowired
    @Qualifier("phoneUserDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired//不需要自己配置，spring自己维护了一个线程池
    private ThreadPoolTaskExecutor executor;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(this.tokenHeader);
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            final String authToken = authHeader.substring(tokenHead.length()); // The part after "Bearer "
            if (authToken != null && redisTemplate.hasKey(RedisUtils.redis_phone_token + authToken)) {
                String phone = redisTemplate.opsForValue().get(RedisUtils.redis_phone_token + authToken);
                //todo 在这里统计用户活跃度
                if (phone != null){
                    User user = userService.selectByPhone(phone);
                    UvStatistic uvStatistic = new UvStatistic();
                    uvStatistic.setSource(0);//其他类型
                    String osType = request.getHeader("osType");
                    if (StringUtils.isNotBlank(osType)){
                        uvStatistic.setSource(Integer.valueOf(osType));
                    }
                    uvStatistic.setType(3);//其他
                    uvStatistic.setUserId(user.getId());
                    executor.execute(()->sendRedisMsg(uvStatistic,phone));
                }

                if (phone != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(phone);
                    //可以校验token和username是否有效，目前由于token对应username存在redis，都以默认都是有效的bv
                    LindTokenAuthenticationToken authentication = new LindTokenAuthenticationToken(userDetails,phone,userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(
                            request));
                    logger.info("authenticated user " + phone + ", setting security context");
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);

        /*String authHeader = request.getHeader(this.tokenHeader);
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            final String authToken = authHeader.substring(tokenHead.length()); // The part after "Bearer "
            if (authToken != null && redisTemplate.hasKey(RedisUtils.redis_phone_token + authToken)) {
                String phone = redisTemplate.opsForValue().get(RedisUtils.redis_phone_token + authToken);
                if (phone != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(phone);
                    //可以校验token和username是否有效，目前由于token对应username存在redis，都以默认都是有效的bv
                    LindTokenAuthenticationToken authentication = new LindTokenAuthenticationToken(userDetails,phone,userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(
                            request));
                    logger.info("authenticated user " + phone + ", setting security context");
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }else {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        }else {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        filterChain.doFilter(request, response);*/
    }

    public void sendRedisMsg(UvStatistic uvStatistic,String phone){
        if (!redisTemplate.hasKey(RedisUtils.redis_uv_prefix + phone)){
            redisTemplate.convertAndSend(RedisUtils.redis_uv_topic, JSONObject.toJSONString(uvStatistic));
            redisTemplate.opsForValue().set(RedisUtils.redis_uv_prefix + phone,"1");
        }
    }
}
