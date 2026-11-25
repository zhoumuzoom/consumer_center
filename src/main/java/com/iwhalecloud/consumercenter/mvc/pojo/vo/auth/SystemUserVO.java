package com.iwhalecloud.consumercenter.mvc.pojo.vo.auth;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 当前登录用户信息VO
 */
@Data
public class SystemUserVO {

    private Long id;

    private String username;

    private String realName;

    private String phone;

    private String email;

    private Integer status;

    private LocalDateTime createTime;
}
