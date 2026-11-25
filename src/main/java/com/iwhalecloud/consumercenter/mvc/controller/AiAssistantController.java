package com.iwhalecloud.consumercenter.mvc.controller;

import com.alibaba.fastjson2.JSON;
import com.iwhalecloud.consumercenter.common.result.Result;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.ai.AiChatRequestDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.ai.AiChatMessageVO;
import com.iwhalecloud.consumercenter.mvc.service.AiAssistantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * AI助手
 */
@Slf4j
@Tag(name = "AI助手", description = "AI流式对话、会话历史、清空会话")
@RestController
@RequestMapping("/api/ai")
public class AiAssistantController {

    @Autowired
    private AiAssistantService aiAssistantService;

    @Operation(summary = "AI助手流式对话")
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chat(@Valid @RequestBody AiChatRequestDTO requestDTO) {
        String conversationId = StringUtils.hasText(requestDTO.getConversationId())
                ? requestDTO.getConversationId()
                : "conv-" + UUID.randomUUID().toString().replace("-", "");
        return aiAssistantService.stream(conversationId, requestDTO.getMessage())
                .filter(StringUtils::hasLength)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .event("delta")
                        .data(JSON.toJSONString(Map.of("content", chunk)))
                        .build())
                .concatWith(Mono.just(ServerSentEvent.<String>builder()
                        .event("done")
                        .data(JSON.toJSONString(Map.of("conversationId", conversationId)))
                        .build()))
                .onErrorResume(e -> {
                    log.error("AI对话失败, conversationId={}", conversationId, e);
                    return Mono.just(ServerSentEvent.<String>builder()
                            .event("error")
                            .data(JSON.toJSONString(Map.of("message", "AI助手响应失败，请稍后重试")))
                            .build());
                });
    }

    @Operation(summary = "获取会话历史")
    @GetMapping("/conversations/{conversationId}/messages")
    public Result<List<AiChatMessageVO>> getHistory(@PathVariable String conversationId) {
        return Result.success(aiAssistantService.getHistory(conversationId));
    }

    @Operation(summary = "清空会话")
    @DeleteMapping("/conversations/{conversationId}")
    public Result<Void> clear(@PathVariable String conversationId) {
        aiAssistantService.clear(conversationId);
        return Result.success();
    }
}