package com.iwhalecloud.consumercenter.mvc.service.impl;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import com.iwhalecloud.consumercenter.common.constant.AuthConstant;
import com.iwhalecloud.consumercenter.common.constant.ErrorMessageEnum;
import com.iwhalecloud.consumercenter.common.error.BusinessException;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.auth.LoginVO;
import com.iwhalecloud.consumercenter.mvc.service.LoginSessionService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 登录会话服务实现
 */
@Service
public class LoginSessionServiceImpl implements LoginSessionService {

    @Override
    public LoginVO issueTokens(Long userId) {
        String accessToken = loginAccessSession(userId);
        String refreshToken = loginRefreshSession(userId);
        return buildLoginVO(accessToken, refreshToken);
    }

    @Override
    public LoginVO refreshTokens(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new BusinessException(ErrorMessageEnum.AUTH_REFRESH_TOKEN_INVALID);
        }
        // 校验refreshToken存在性（忽略冻结状态）
        Object loginId = StpUtil.stpLogic.getLoginIdByTokenNotThinkFreeze(refreshToken);
        if (loginId == null) {
            throw new BusinessException(ErrorMessageEnum.AUTH_REFRESH_TOKEN_INVALID);
        }
        // 校验会话类型为refresh，防止accessToken被当作刷新令牌使用
        if (!AuthConstant.DEVICE_REFRESH.equals(StpUtil.getLoginDeviceTypeByToken(refreshToken))) {
            throw new BusinessException(ErrorMessageEnum.AUTH_REFRESH_TOKEN_INVALID);
        }
        // 校验活跃窗口，闲置超过7天视为过期
        if (StpUtil.stpLogic.getTokenActiveTimeoutByToken(refreshToken) == SaTokenDao.NOT_VALUE_EXPIRE) {
            throw new BusinessException(ErrorMessageEnum.AUTH_REFRESH_TOKEN_EXPIRED);
        }

        // 签发新的accessToken
        String accessToken = loginAccessSession(Long.parseLong(loginId.toString()));
        // refreshToken滑动续期：续期Redis有效期并刷新最后活跃时间
        StpUtil.renewTimeout(refreshToken, AuthConstant.REFRESH_TOKEN_REDIS_TIMEOUT);
        StpUtil.stpLogic.updateLastActiveToNow(refreshToken);
        return buildLoginVO(accessToken, refreshToken);
    }

    @Override
    public void invalidateSessions(String refreshToken) {
        String currentLoginId = StpUtil.getLoginIdAsString();
        // 作废当前accessToken会话
        StpUtil.logout();
        // 携带了合法的refreshToken则一并作废
        if (StringUtils.hasText(refreshToken)) {
            Object loginId = StpUtil.stpLogic.getLoginIdByTokenNotThinkFreeze(refreshToken);
            boolean sameUser = loginId != null && Objects.equals(loginId.toString(), currentLoginId);
            boolean refreshSession = AuthConstant.DEVICE_REFRESH.equals(StpUtil.getLoginDeviceTypeByToken(refreshToken));
            if (sameUser && refreshSession) {
                StpUtil.logoutByTokenValue(refreshToken);
            }
        }
    }

    /**
     * 登录access会话，返回accessToken
     */
    private String loginAccessSession(Long userId) {
        StpUtil.login(userId, new SaLoginParameter()
                .setDeviceType(AuthConstant.DEVICE_ACCESS)
                .setTimeout(AuthConstant.ACCESS_TOKEN_REDIS_TIMEOUT)
                .setActiveTimeout(AuthConstant.ACCESS_TOKEN_ACTIVE_TIMEOUT));
        return StpUtil.getTokenValue();
    }

    /**
     * 登录refresh会话，返回refreshToken
     */
    private String loginRefreshSession(Long userId) {
        StpUtil.login(userId, new SaLoginParameter()
                .setDeviceType(AuthConstant.DEVICE_REFRESH)
                .setTimeout(AuthConstant.REFRESH_TOKEN_REDIS_TIMEOUT)
                .setActiveTimeout(AuthConstant.REFRESH_TOKEN_ACTIVE_TIMEOUT));
        return StpUtil.getTokenValue();
    }

    /**
     * 组装登录结果
     */
    private LoginVO buildLoginVO(String accessToken, String refreshToken) {
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setAccessTokenExpiresIn(AuthConstant.ACCESS_TOKEN_ACTIVE_TIMEOUT);
        loginVO.setRefreshTokenExpiresIn(AuthConstant.REFRESH_TOKEN_ACTIVE_TIMEOUT);
        return loginVO;
    }
}
