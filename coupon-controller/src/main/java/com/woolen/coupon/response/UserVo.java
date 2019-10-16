package com.woolen.coupon.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @Info:
 * @ClassName: UserVo
 * @Author: weiyang
 * @Data: 2019/10/15 11:16 AM
 * @Version: V1.0
 **/
@Data
public class UserVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;

    private String phone;

    private String token;


}
