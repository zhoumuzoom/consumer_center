package com.iwhalecloud.consumercenter.mvc.service;

import com.iwhalecloud.consumercenter.mvc.pojo.vo.auth.LoginVO;

/**
 * 登录会话服务
 * <p>负责双token的完整生命周期：签发、刷新、作废。
 * 用户身份无论经由哪个认证通道确认（本地账号密码、门户SSO等），
 * 都统一通过本服务签发令牌，认证方式与令牌机制互相解耦。
 */
public interface LoginSessionService {

    /**
     * 为用户签发双token（登录场景）
     *
     * @param userId 用户ID
     * @return 登录结果
     */
    LoginVO issueTokens(Long userId);

    /**
     * 刷新令牌：校验refreshToken，签发新accessToken，并对refreshToken滑动续期
     *
     * @param refreshToken 刷新令牌
     * @return 登录结果（新accessToken，原refreshToken续期）
     */
    LoginVO refreshTokens(String refreshToken);

    /**
     * 作废当前会话的accessToken，携带合法refreshToken时一并作废
     *
     * @param refreshToken 刷新令牌（可为空）
     */
    void invalidateSessions(String refreshToken);
}
