package com.iwhalecloud.consumercenter.mvc.pojo.vo.auth;

import lombok.Data;

/**
 * 登录结果VO
 */
@Data
public class LoginVO {

    private String accessToken;

    private String refreshToken;

    private Long accessTokenExpiresIn;

    private Long refreshTokenExpiresIn;
}
