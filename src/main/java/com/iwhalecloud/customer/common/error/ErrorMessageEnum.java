package com.iwhalecloud.customer.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 异常信息枚举
 */
@Getter
@AllArgsConstructor
public enum ErrorMessageEnum {

    SERIVCE_ERROR(5001, "系统异常"),

    BUSINESS_ERROR(5002, "业务错误"),

    AUTH_USERNAME_OR_PASSWORD_INVALID(4001, "用户名或密码错误"),
    AUTH_TOKEN_MISSING(4002, "未提供认证令牌"),
    AUTH_TOKEN_EXPIRED(4003, "认证令牌已过期"),
    AUTH_TOKEN_INVALID(4004, "认证令牌无效"),
    AUTH_REFRESH_TOKEN_INVALID(4005, "刷新令牌无效"),
    AUTH_REFRESH_TOKEN_EXPIRED(4006, "刷新令牌已过期");

    private final int code;

    private final String message;
}
