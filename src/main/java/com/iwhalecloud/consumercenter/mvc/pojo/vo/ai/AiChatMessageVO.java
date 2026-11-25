package com.iwhalecloud.consumercenter.mvc.pojo.vo.ai;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * AI对话消息VO
 */
@Data
@Schema(description = "AI对话消息")
public class AiChatMessageVO {

    @Schema(description = "角色(user/assistant)")
    private String role;

    @Schema(description = "消息内容")
    private String content;
}