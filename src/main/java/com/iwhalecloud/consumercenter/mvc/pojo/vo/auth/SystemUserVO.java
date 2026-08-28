package com.iwhalecloud.consumercenter.mvc.pojo.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 当前登录用户信息VO
 */
@Data
@Schema(description = "当前登录用户信息")
public class SystemUserVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "状态(1:启用,0:禁用)")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
