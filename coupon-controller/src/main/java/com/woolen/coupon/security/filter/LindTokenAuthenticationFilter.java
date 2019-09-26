package com.woolen.coupon.security.filter;

import com.woolen.coupon.security.token.LindTokenAuthenticationToken;
import com.woolen.coupon.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(this.tokenHeader);
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
}
