package com.iwhalecloud.consumercenter.mvc.pojo.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录结果VO
 */
@Data
@Schema(description = "登录结果")
public class LoginVO {

    @Schema(description = "访问令牌")
    private String accessToken;

    @Schema(description = "刷新令牌")
    private String refreshToken;

    @Schema(description = "访问令牌滑动闲置窗口（秒），闲置超时后需刷新")
    private Long accessTokenExpiresIn;

    @Schema(description = "刷新令牌滑动闲置窗口（秒），闲置超时后需重新登录")
    private Long refreshTokenExpiresIn;
}
