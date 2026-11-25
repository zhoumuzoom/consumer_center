package com.iwhalecloud.consumercenter.mvc.pojo.vo.ai;

import lombok.Data;

/**
 * AI对话消息VO
 */
@Data
public class AiChatMessageVO {

    private String role;

    private String content;
}