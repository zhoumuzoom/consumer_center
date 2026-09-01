package com.iwhalecloud.consumercenter.mvc.service;

import com.iwhalecloud.consumercenter.mvc.pojo.dto.auth.LoginDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.auth.SsoLoginDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.auth.LoginVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.auth.SystemUserVO;

/**
 * 认证服务
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param loginDTO 登录请求
     * @return 登录结果（含双token）
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * OAuth2 SSO登录
     *
     * @param ssoLoginDTO SSO登录请求
     * @return 登录结果（含双token）
     */
    LoginVO ssoLogin(SsoLoginDTO ssoLoginDTO);

    /**
     * 刷新accessToken
     *
     * @param refreshToken 刷新令牌
     * @return 登录结果（新accessToken，refreshToken滑动续期）
     */
    LoginVO refresh(String refreshToken);

    /**
     * 退出登录
     *
     * @param refreshToken 刷新令牌（可为空）
     */
    void logout(String refreshToken);

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    SystemUserVO getUserInfo();
}
