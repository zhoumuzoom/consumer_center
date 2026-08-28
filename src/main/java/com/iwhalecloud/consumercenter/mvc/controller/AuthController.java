package com.iwhalecloud.consumercenter.mvc.controller;

import com.iwhalecloud.consumercenter.common.constant.AuthConstant;
import com.iwhalecloud.consumercenter.common.result.Result;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.auth.LoginDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.auth.LoginVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.auth.SystemUserVO;
import com.iwhalecloud.consumercenter.mvc.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "认证管理", description = "用户登录、令牌刷新、退出登录、当前用户信息")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return Result.success(authService.login(loginDTO));
    }

    @Operation(summary = "刷新accessToken", description = "通过请求头refresh-token携带刷新令牌")
    @PostMapping("/refresh")
    public Result<LoginVO> refresh(HttpServletRequest request) {
        return Result.success(authService.refresh(request.getHeader(AuthConstant.REFRESH_TOKEN_HEADER)));
    }

    @Operation(summary = "退出登录", description = "作废当前accessToken，可通过请求头refresh-token一并作废刷新令牌")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        authService.logout(request.getHeader(AuthConstant.REFRESH_TOKEN_HEADER));
        return Result.success();
    }

    @Operation(summary = "当前登录用户信息")
    @GetMapping("/userinfo")
    public Result<SystemUserVO> userInfo() {
        return Result.success(authService.getUserInfo());
    }
}
