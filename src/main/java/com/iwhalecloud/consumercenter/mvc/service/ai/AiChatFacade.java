package com.iwhalecloud.consumercenter.mvc.service.ai;

import reactor.core.publisher.Flux;

import java.util.List;

/**
 * AI对话统一入口
 */
public interface AiChatFacade {

    /** 单轮结构化调用：返回指定类型的模型输出 */
    <T> T callForEntity(String systemPrompt, String userPrompt, Class<T> responseType);

    /** 带会话记忆的流式对话，可挂载工具 */
    Flux<String> streamWithMemory(String conversationId, String systemPrompt, String userMessage, List<Object> tools);
}