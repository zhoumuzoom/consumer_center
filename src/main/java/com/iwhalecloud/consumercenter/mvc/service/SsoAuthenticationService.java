package com.iwhalecloud.consumercenter.mvc.service;

import com.iwhalecloud.consumercenter.mvc.pojo.vo.auth.LoginVO;

/**
 * 外部单点登录认证服务
 */
public interface SsoAuthenticationService {

    /**
     * 远程校验OAuth2访问令牌并签发本系统双Token
     *
     * @param externalToken 门户OAuth2访问令牌
     * @return 登录结果
     */
    LoginVO login(String externalToken);
}
