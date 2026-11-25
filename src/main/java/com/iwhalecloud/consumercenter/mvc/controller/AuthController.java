package com.iwhalecloud.consumercenter.mvc.controller;

import com.iwhalecloud.consumercenter.common.constant.AuthConstant;
import com.iwhalecloud.consumercenter.common.result.Result;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.auth.LoginDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.auth.SsoLoginDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.auth.LoginVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.auth.SystemUserVO;
import com.iwhalecloud.consumercenter.mvc.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证管理
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return Result.success(authService.login(loginDTO));
    }

    @PostMapping("/sso-login")
    public Result<LoginVO> ssoLogin(@Valid @RequestBody SsoLoginDTO ssoLoginDTO) {
        return Result.success(authService.ssoLogin(ssoLoginDTO));
    }

    @PostMapping("/refresh")
    public Result<LoginVO> refresh(HttpServletRequest request) {
        return Result.success(authService.refresh(request.getHeader(AuthConstant.REFRESH_TOKEN_HEADER)));
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        authService.logout(request.getHeader(AuthConstant.REFRESH_TOKEN_HEADER));
        return Result.success();
    }

    @GetMapping("/userinfo")
    public Result<SystemUserVO> userInfo() {
        return Result.success(authService.getUserInfo());
    }
}
