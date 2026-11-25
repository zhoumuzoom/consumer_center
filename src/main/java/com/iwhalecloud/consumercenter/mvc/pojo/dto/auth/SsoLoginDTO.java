package com.iwhalecloud.consumercenter.mvc.pojo.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * OAuth2 SSO登录请求
 */
@Data
public class SsoLoginDTO {

    @NotBlank(message = "外部访问令牌不能为空")
    private String externalToken;
}
