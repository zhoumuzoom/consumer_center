package com.iwhalecloud.consumercenter.mvc.service.ai.impl;

import com.iwhalecloud.consumercenter.mvc.service.ai.AiChatFacade;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * AI对话统一入口实现
 */
@Service
public class AiChatFacadeImpl implements AiChatFacade {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ChatMemory chatMemory;

    @Override
    public <T> T callForEntity(String systemPrompt, String userPrompt, Class<T> responseType) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .entity(responseType);
    }

    @Override
    public Flux<String> streamWithMemory(String conversationId, String systemPrompt, String userMessage, List<Object> tools) {
        ChatClient.ChatClientRequestSpec requestSpec = chatClient.prompt()
                .system(systemPrompt)
                .user(userMessage)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId));
        if (tools != null && !tools.isEmpty()) {
            requestSpec.tools(tools.toArray());
        }
        return requestSpec.stream().content();
    }
}