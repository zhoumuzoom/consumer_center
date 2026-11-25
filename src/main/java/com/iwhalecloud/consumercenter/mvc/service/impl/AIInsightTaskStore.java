package com.iwhalecloud.consumercenter.mvc.service.impl;

import com.alibaba.fastjson2.JSON;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.profile.AIInsightResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * AI洞察任务状态存储
 */
@Component
public class AIInsightTaskStore {

    private static final String KEY_PREFIX = "consumer:ai-insight:";

    private static final String LOCK_PREFIX = "consumer:ai-insight:lock:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${ai.insight.task-ttl-hours:2}")
    private long taskTtlHours;

    @Value("${ai.insight.lock-ttl-seconds:180}")
    private long lockTtlSeconds;

    public AIInsightResultVO get(Long customerId) {
        String json = redisTemplate.opsForValue().get(KEY_PREFIX + customerId);
        if (json == null || json.isEmpty()) {
            return null;
        }
        return JSON.parseObject(json, AIInsightResultVO.class);
    }

    public void save(Long customerId, AIInsightResultVO result) {
        redisTemplate.opsForValue().set(KEY_PREFIX + customerId, JSON.toJSONString(result),
                Duration.ofHours(taskTtlHours));
    }

    public void delete(Long customerId) {
        redisTemplate.delete(KEY_PREFIX + customerId);
    }

    public boolean tryLock(Long customerId, String taskId) {
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(LOCK_PREFIX + customerId, taskId, Duration.ofSeconds(lockTtlSeconds));
        return Boolean.TRUE.equals(acquired);
    }

    public void unlock(Long customerId, String taskId) {
        String lockKey = LOCK_PREFIX + customerId;
        String currentTaskId = redisTemplate.opsForValue().get(lockKey);
        if (taskId.equals(currentTaskId)) {
            redisTemplate.delete(lockKey);
        }
    }
}