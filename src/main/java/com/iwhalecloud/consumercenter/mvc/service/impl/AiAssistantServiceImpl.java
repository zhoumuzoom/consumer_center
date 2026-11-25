package com.iwhalecloud.consumercenter.mvc.service.impl;

import com.iwhalecloud.consumercenter.common.constant.ErrorMessageEnum;
import com.iwhalecloud.consumercenter.common.error.BusinessException;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.ai.AiChatMessageVO;
import com.iwhalecloud.consumercenter.mvc.service.AiAssistantService;
import com.iwhalecloud.consumercenter.mvc.service.ai.AiChatFacade;
import com.iwhalecloud.consumercenter.mvc.service.ai.tool.CustomerQueryTools;
import com.iwhalecloud.consumercenter.mvc.service.ai.tool.DecisionTools;
import com.iwhalecloud.consumercenter.mvc.service.ai.tool.ProfileQueryTools;
import com.iwhalecloud.consumercenter.mvc.service.ai.tool.ReportTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * AI助手服务实现
 */
@Slf4j
@Service
public class AiAssistantServiceImpl implements AiAssistantService {

    @Autowired
    private AiChatFacade aiChatFacade;

    @Autowired
    private ChatMemory chatMemory;

    @Autowired
    private CustomerQueryTools customerQueryTools;

    @Autowired
    private ProfileQueryTools profileQueryTools;

    @Autowired
    private DecisionTools decisionTools;

    @Autowired
    private ReportTools reportTools;

    @Value("classpath:ai/prompts/copilot-system.st")
    private Resource systemPromptResource;

    @Value("${spring.ai.openai.api-key:}")
    private String apiKey;

    @Override
    public Flux<String> stream(String conversationId, String userMessage) {
        if (!StringUtils.hasText(apiKey) || "unset".equals(apiKey)) {
            return Flux.error(new BusinessException(ErrorMessageEnum.AI_NOT_CONFIGURED));
        }
        return aiChatFacade.streamWithMemory(conversationId, readSystemPrompt(), userMessage,
                List.of(customerQueryTools, profileQueryTools, decisionTools, reportTools));
    }

    @Override
    public List<AiChatMessageVO> getHistory(String conversationId) {
        List<Message> messages = chatMemory.get(conversationId);
        List<AiChatMessageVO> history = new ArrayList<>();
        if (messages == null) {
            return history;
        }
        for (Message message : messages) {
            if (message.getMessageType() != MessageType.USER && message.getMessageType() != MessageType.ASSISTANT) {
                continue;
            }
            AiChatMessageVO item = new AiChatMessageVO();
            item.setRole(message.getMessageType().getValue());
            item.setContent(message.getText());
            history.add(item);
        }
        return history;
    }

    @Override
    public void clear(String conversationId) {
        chatMemory.clear(conversationId);
    }

    private String readSystemPrompt() {
        try (InputStream inputStream = systemPromptResource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("加载AI助手系统提示失败", e);
            throw new BusinessException("AI助手系统提示加载失败");
        }
    }
}