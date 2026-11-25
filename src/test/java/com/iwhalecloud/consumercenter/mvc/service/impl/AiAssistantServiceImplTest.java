package com.iwhalecloud.consumercenter.mvc.service.impl;

import com.iwhalecloud.consumercenter.common.constant.ErrorMessageEnum;
import com.iwhalecloud.consumercenter.common.error.BusinessException;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.ai.AiChatMessageVO;
import com.iwhalecloud.consumercenter.mvc.service.ai.AiChatFacade;
import com.iwhalecloud.consumercenter.mvc.service.ai.tool.CustomerQueryTools;
import com.iwhalecloud.consumercenter.mvc.service.ai.tool.DecisionTools;
import com.iwhalecloud.consumercenter.mvc.service.ai.tool.ProfileQueryTools;
import com.iwhalecloud.consumercenter.mvc.service.ai.tool.ReportTools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.session.SessionService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AI助手服务测试
 */
@ExtendWith(MockitoExtension.class)
class AiAssistantServiceImplTest {

    @InjectMocks
    private AiAssistantServiceImpl aiAssistantService;

    @Mock
    private AiChatFacade aiChatFacade;

    @Mock
    private SessionService sessionService;

    @Mock
    private CustomerQueryTools customerQueryTools;

    @Mock
    private ProfileQueryTools profileQueryTools;

    @Mock
    private DecisionTools decisionTools;

    @Mock
    private ReportTools reportTools;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(aiAssistantService, "systemPromptResource",
                new ByteArrayResource("你是CMI要客运营平台AI助手".getBytes(StandardCharsets.UTF_8)));
        ReflectionTestUtils.setField(aiAssistantService, "apiKey", "test-key");
    }

    @Test
    @DisplayName("网关未配置时返回AI未配置错误")
    void stream_whenApiKeyMissing_returnsNotConfiguredError() {
        ReflectionTestUtils.setField(aiAssistantService, "apiKey", "unset");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> aiAssistantService.stream("c1", "你好").collectList().block());

        assertEquals(ErrorMessageEnum.AI_NOT_CONFIGURED.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("流式对话携带会话ID、系统提示和4个工具")
    void stream_delegatesToFacadeWithTools() {
        when(aiChatFacade.streamWithMemory(eq("c1"), anyString(), eq("查一下客户"), anyList()))
                .thenReturn(Flux.just("正在", "查询"));

        List<String> chunks = aiAssistantService.stream("c1", "查一下客户").collectList().block();

        assertEquals(List.of("正在", "查询"), chunks);
        verify(aiChatFacade).streamWithMemory(eq("c1"), anyString(), eq("查一下客户"), anyList());
    }

    @Test
    @DisplayName("历史记录只保留用户和助手消息")
    void getHistory_filtersSystemMessages() {
        when(sessionService.getMessages("c1")).thenReturn(List.of(
                new SystemMessage("系统提示"),
                new UserMessage("你好"),
                new AssistantMessage(""),
                new AssistantMessage("您好")));

        List<AiChatMessageVO> history = aiAssistantService.getHistory("c1");

        assertEquals(2, history.size());
        assertEquals("user", history.get(0).getRole());
        assertEquals("你好", history.get(0).getContent());
        assertEquals("assistant", history.get(1).getRole());
        assertFalse(history.get(1).getContent().isEmpty());
    }

    @Test
    @DisplayName("清空会话委托给会话服务")
    void clear_delegatesToSessionService() {
        aiAssistantService.clear("c1");

        verify(sessionService).delete("c1");
    }
}