package com.woolen.coupon.security.config;

import com.woolen.coupon.security.entryPoint.LoginJsonAuthenticationEntryPoint;
import com.woolen.coupon.security.evaluator.CustomPermissionEvaluator;
import com.woolen.coupon.security.filter.VerifyFilter;
import com.woolen.coupon.security.handler.CustomAccessDeniedHandler;
import com.woolen.coupon.security.handler.CustomAuthenticationFailureHandler;
import com.woolen.coupon.security.handler.CustomAuthenticationSuccessHandler;
import com.woolen.coupon.security.securityService.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

/**
 * @Info:
 * @ClassName: WebSecurityConfig
 * @Author: weiyang
 * @Data: 2019/9/20 9:32 PM
 * @Version: V1.0
 **/

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Autowired
    private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;

    @Autowired
    LindTokenAuthenticationSecurityConfig lindTokenAuthenticationSecurityConfig;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 设置拦截忽略文件夹，可以对静态资源放行
        web.ignoring().antMatchers("/css/**", "/js/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.apply(lindTokenAuthenticationSecurityConfig);
        http.apply(smsCodeAuthenticationSecurityConfig)
                .and().authorizeRequests()
                // 如果有允许匿名的url，填在下面
// 如果有允许匿名的url，填在下面
                .antMatchers("/getVerifyCode","/sms/**","/login","/lind-auth/**").permitAll()
                .anyRequest().authenticated()
                .and()
                //无权限用户提示字符串消息设置
                .exceptionHandling()
                // getAccessDeniedHandler()是上文的方法
                .accessDeniedHandler(customAccessDeniedHandler)
                .and()
                // 设置登陆页
                .formLogin().loginPage("/login")
                //登陆成功和失败的自定义处理器，不能和默认处理方式同时存在
                .successHandler(customAuthenticationSuccessHandler)
                .failureHandler(customAuthenticationFailureHandler)
                // 设置登陆成功页
               /* .defaultSuccessUrl("/").permitAll()
                // 登录失败Url
                .failureUrl("/login/error")*/
                // 自定义登陆用户名和密码参数，默认为username和password
//                .usernameParameter("username")
//                .passwordParameter("password")
                .and()
                //验证码过滤器
                .addFilterBefore(new VerifyFilter(),UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(lindTokenAuthenticationFilter ,UsernamePasswordAuthenticationFilter.class)
                .logout().permitAll()
                // 自动登录
                .and().rememberMe()
                //数据库处理token
                .tokenRepository(persistentTokenRepository())
                // 有效时间：单位s
                .tokenValiditySeconds(600)
                .userDetailsService(userDetailsService);
                //为登陆访问返回json处理
        http.exceptionHandling().authenticationEntryPoint(new LoginJsonAuthenticationEntryPoint());
        // 关闭CSRF跨域
        http.csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //密码加密就用下面这个
//        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
        auth.userDetailsService(userDetailsService).passwordEncoder(new PasswordEncoder() {
            @Override
            public String encode(CharSequence charSequence) {
                return charSequence.toString();
            }

            @Override
            public boolean matches(CharSequence charSequence, String s) {
                return s.equals(charSequence.toString());
            }
        });
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        // 如果token表不存在，使用下面语句可以初始化该表；若存在，请注释掉这条语句，否则会报错。
//        tokenRepository.setCreateTableOnStartup(true);
        return tokenRepository;
    }

    /**
     * 注入自定义PermissionEvaluator
     */
    @Bean
    public DefaultWebSecurityExpressionHandler defaulwebSecurityExpressionHandler(){
        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setPermissionEvaluator(new CustomPermissionEvaluator());
        return handler;
    }

   /* @Bean
    public AccessDeniedHandler getAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }*/
}
