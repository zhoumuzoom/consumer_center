package com.iwhalecloud.consumercenter.mvc.service.ai.memory;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 基于Redis的会话记忆仓库
 */
@Repository
public class RedisChatMemoryRepository implements ChatMemoryRepository {

    private static final String KEY_PREFIX = "consumer:ai:memory:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${ai.copilot.memory-ttl-hours:24}")
    private long memoryTtlHours;

    @Override
    public List<String> findConversationIds() {
        Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> conversationIds = new ArrayList<>(keys.size());
        for (String key : keys) {
            conversationIds.add(key.substring(KEY_PREFIX.length()));
        }
        return conversationIds;
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        String json = redisTemplate.opsForValue().get(buildKey(conversationId));
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        List<StoredMessage> storedMessages = JSON.parseArray(json, StoredMessage.class);
        List<Message> messages = new ArrayList<>(storedMessages.size());
        for (StoredMessage stored : storedMessages) {
            Message message = toMessage(stored);
            if (message != null) {
                messages.add(message);
            }
        }
        return messages;
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        List<StoredMessage> storedMessages = new ArrayList<>();
        if (messages != null) {
            for (Message message : messages) {
                StoredMessage stored = fromMessage(message);
                if (stored != null) {
                    storedMessages.add(stored);
                }
            }
        }
        redisTemplate.opsForValue().set(buildKey(conversationId), JSON.toJSONString(storedMessages),
                Duration.ofHours(memoryTtlHours));
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        redisTemplate.delete(buildKey(conversationId));
    }

    private String buildKey(String conversationId) {
        return KEY_PREFIX + conversationId;
    }

    private StoredMessage fromMessage(Message message) {
        if (message == null || message.getMessageType() == MessageType.TOOL) {
            return null;
        }
        String text = message.getText();
        if (text == null || text.isBlank()) {
            return null;
        }
        StoredMessage stored = new StoredMessage();
        stored.setRole(message.getMessageType().name());
        stored.setContent(text);
        return stored;
    }

    private Message toMessage(StoredMessage stored) {
        if (stored == null || stored.getContent() == null || stored.getRole() == null) {
            return null;
        }
        if (MessageType.USER.name().equals(stored.getRole())) {
            return new UserMessage(stored.getContent());
        }
        if (MessageType.ASSISTANT.name().equals(stored.getRole())) {
            return new AssistantMessage(stored.getContent());
        }
        if (MessageType.SYSTEM.name().equals(stored.getRole())) {
            return new SystemMessage(stored.getContent());
        }
        return null;
    }

    /**
     * Redis中存储的消息结构
     */
    @Data
    public static class StoredMessage {
        private String role;
        private String content;
    }
}