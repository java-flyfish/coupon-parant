package com.woolen.coupon.utils;

/**
 * @Info:
 * @ClassName: utils
 * @Author: weiyang
 * @Data: 2019/9/24 4:10 PM
 * @Version: V1.0
 **/
public class RedisUtils {
    //短信验证码前缀
    public static String redis_phone_code = "redis_phone_code:";
    //用户app登陆token前缀
    public static String redis_phone_token = "redis_phone_token:";

    //用户活跃度topic
    public static String redis_uv_topic = "redis_uv_topic";

    //用户今天是否活跃前缀
    public static String redis_uv_prefix = "redis_uv_prefix:";


}
