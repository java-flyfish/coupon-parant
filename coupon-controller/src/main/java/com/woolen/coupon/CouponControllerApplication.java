package com.woolen.coupon;

import com.woolen.coupon.security.servlet.VerifyServlet;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan("com.woolen.coupon.mapper")
public class CouponControllerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CouponControllerApplication.class, args);
	}

	/**
	 * 注入验证码servlet
	 */
	@Bean
	public ServletRegistrationBean indexServletRegistration() {
		ServletRegistrationBean registration = new ServletRegistrationBean(new VerifyServlet());
		registration.addUrlMappings("/getVerifyCode");
		return registration;
	}
}
