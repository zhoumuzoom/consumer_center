package com.iwhalecloud.consumercenter.interceptor;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import com.iwhalecloud.consumercenter.common.constant.AuthConstant;
import com.iwhalecloud.consumercenter.common.constant.ErrorMessageEnum;
import com.iwhalecloud.consumercenter.common.error.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器，校验请求头中的accessToken
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // sa-token从名为token的请求头读取令牌并校验登录状态
        try {
            StpUtil.checkLogin();
        } catch (NotLoginException e) {
            throw mapNotLoginException(e);
        }
        // refreshToken不允许当作业务接口的访问令牌使用
        if (!AuthConstant.DEVICE_ACCESS.equals(StpUtil.getLoginDeviceTypeByToken(StpUtil.getTokenValue()))) {
            throw new BusinessException(ErrorMessageEnum.AUTH_TOKEN_INVALID);
        }
        return true;
    }

    /**
     * 未登录异常映射为业务异常
     */
    private BusinessException mapNotLoginException(NotLoginException e) {
        return switch (e.getType()) {
            // 未携带token
            case NotLoginException.NOT_TOKEN -> new BusinessException(ErrorMessageEnum.AUTH_TOKEN_MISSING);
            // 活跃窗口超时（冻结）或到期过期，前端应尝试刷新令牌
            case NotLoginException.TOKEN_FREEZE, NotLoginException.TOKEN_TIMEOUT ->
                    new BusinessException(ErrorMessageEnum.AUTH_TOKEN_EXPIRED);
            // 伪造、已登出、被顶替、被踢下线等
            default -> new BusinessException(ErrorMessageEnum.AUTH_TOKEN_INVALID);
        };
    }
}
