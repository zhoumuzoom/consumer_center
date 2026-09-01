package com.iwhalecloud.consumercenter.mvc.pojo.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * OAuth2 SSO登录请求
 */
@Data
@Schema(description = "OAuth2 SSO登录请求")
public class SsoLoginDTO {

    @NotBlank(message = "外部访问令牌不能为空")
    @Schema(description = "门户OAuth2访问令牌", requiredMode = Schema.RequiredMode.REQUIRED)
    private String externalToken;
}
