package com.iwhalecloud.consumercenter.mvc.pojo.dto.ai;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * AI对话请求DTO
 */
@Data
@Schema(description = "AI对话请求")
public class AiChatRequestDTO {

    @Schema(description = "会话ID，为空时由服务端生成")
    private String conversationId;

    @Schema(description = "用户消息", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "消息不能为空")
    @Size(max = 2000, message = "消息不能超过2000字")
    private String message;
}