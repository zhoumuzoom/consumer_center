package com.iwhalecloud.consumercenter.mvc.pojo.dto.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * AI对话请求DTO
 */
@Data
public class AiChatRequestDTO {

    private String conversationId;

    @NotBlank(message = "消息不能为空")
    @Size(max = 2000, message = "消息不能超过2000字")
    private String message;
}