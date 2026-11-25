package com.iwhalecloud.consumercenter.mvc.service;

import com.iwhalecloud.consumercenter.mvc.pojo.vo.ai.AiChatMessageVO;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * AI助手服务
 */
public interface AiAssistantService {

    /** 流式对话 */
    Flux<String> stream(String conversationId, String userMessage);

    /** 获取会话历史 */
    List<AiChatMessageVO> getHistory(String conversationId);

    /** 清空会话 */
    void clear(String conversationId);
}