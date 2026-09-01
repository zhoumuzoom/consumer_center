package com.iwhalecloud.consumercenter.mvc.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iwhalecloud.consumercenter.common.constant.ErrorMessageEnum;
import com.iwhalecloud.consumercenter.common.error.BusinessException;
import com.iwhalecloud.consumercenter.mvc.mapper.SystemUserMapper;
import com.iwhalecloud.consumercenter.mvc.pojo.convert.SystemUserConvert;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.auth.LoginDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.auth.SsoLoginDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.SystemUser;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.auth.LoginVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.auth.SystemUserVO;
import com.iwhalecloud.consumercenter.mvc.service.AuthService;
import com.iwhalecloud.consumercenter.mvc.service.LoginSessionService;
import com.iwhalecloud.consumercenter.mvc.service.SsoAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 认证服务实现（本地账号密码及OAuth2 SSO通道）
 * <p>不同认证通道完成身份校验后，统一委托会话服务签发双token，
 * 令牌机制保持不变。
 */
@Service
public class AuthServiceImpl implements AuthService {

    /**
     * 用户启用状态
     */
    private static final Byte USER_STATUS_ENABLED = 1;

    @Autowired
    private SystemUserMapper systemUserMapper;

    @Autowired
    private LoginSessionService loginSessionService;

    @Autowired
    private SsoAuthenticationService ssoAuthenticationService;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        SystemUser user = systemUserMapper.selectOne(new LambdaQueryWrapper<SystemUser>()
                .eq(SystemUser::getUsername, loginDTO.getUsername()));
        if (user == null || !Objects.equals(user.getPassword(), loginDTO.getPassword())) {
            throw new BusinessException(ErrorMessageEnum.AUTH_USERNAME_OR_PASSWORD_INVALID);
        }
        if (!USER_STATUS_ENABLED.equals(user.getStatus())) {
            throw new BusinessException(ErrorMessageEnum.AUTH_USER_DISABLED);
        }
        return loginSessionService.issueTokens(user.getId());
    }

    @Override
    public LoginVO ssoLogin(SsoLoginDTO ssoLoginDTO) {
        return ssoAuthenticationService.login(ssoLoginDTO.getExternalToken());
    }

    @Override
    public LoginVO refresh(String refreshToken) {
        return loginSessionService.refreshTokens(refreshToken);
    }

    @Override
    public void logout(String refreshToken) {
        loginSessionService.invalidateSessions(refreshToken);
    }

    @Override
    public SystemUserVO getUserInfo() {
        long userId = StpUtil.getLoginIdAsLong();
        SystemUser user = systemUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorMessageEnum.AUTH_TOKEN_INVALID);
        }
        return convertToUserVO(user);
    }

    /**
     * 用户实体转用户信息VO（不含密码及外部平台标识）
     */
    private SystemUserVO convertToUserVO(SystemUser user) {
        return SystemUserConvert.entity2vo(user);
    }
}
