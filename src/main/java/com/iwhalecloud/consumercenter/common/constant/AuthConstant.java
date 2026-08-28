package com.iwhalecloud.consumercenter.common.constant;

/**
 * 认证相关常量
 */
public class AuthConstant {

    private AuthConstant() {
    }

    /**
     * accessToken会话设备类型
     */
    public static final String DEVICE_ACCESS = "access";

    /**
     * refreshToken会话设备类型
     */
    public static final String DEVICE_REFRESH = "refresh";

    /**
     * 刷新令牌请求头名称
     */
    public static final String REFRESH_TOKEN_HEADER = "refresh-token";

    /**
     * accessToken活跃窗口（秒），闲置超过该值视为过期
     */
    public static final long ACCESS_TOKEN_ACTIVE_TIMEOUT = 30 * 60L;

    /**
     * accessToken在Redis中的绝对有效期（秒），需大于活跃窗口以便区分过期与无效
     */
    public static final long ACCESS_TOKEN_REDIS_TIMEOUT = 7 * 24 * 60 * 60L;

    /**
     * refreshToken活跃窗口（秒），闲置超过该值视为过期
     */
    public static final long REFRESH_TOKEN_ACTIVE_TIMEOUT = 7 * 24 * 60 * 60L;

    /**
     * refreshToken在Redis中的绝对有效期（秒），每次刷新时续期
     */
    public static final long REFRESH_TOKEN_REDIS_TIMEOUT = 14 * 24 * 60 * 60L;
}
