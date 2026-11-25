package com.iwhalecloud.consumercenter.common.constant;

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
    AUTH_REFRESH_TOKEN_EXPIRED(4006, "刷新令牌已过期"),
    AUTH_USER_DISABLED(4007, "账号已被禁用"),
    AUTH_SSO_TOKEN_INVALID(4008, "SSO令牌无效"),
    AUTH_SSO_USER_NOT_FOUND(4009, "SSO用户未绑定"),
    AUTH_SSO_NOT_CONFIGURED(4010, "SSO未配置"),
    AUTH_SSO_PROVIDER_UNAVAILABLE(4011, "SSO服务不可用"),

    AI_INSIGHT_TASK_NOT_FOUND(4101, "洞察任务不存在或已过期"),
    AI_INSIGHT_NOT_CONFIGURED(4102, "AI网关未配置"),
    AI_INSIGHT_GENERATE_FAILED(4103, "AI洞察生成失败"),
    AI_INSIGHT_BUSY(4104, "AI洞察任务繁忙，请稍后重试"),
    AI_NOT_CONFIGURED(4105, "AI网关未配置");

    private final int code;

    private final String message;
}
