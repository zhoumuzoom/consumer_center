package com.iwhalecloud.consumercenter.mvc.service.impl;

import com.iwhalecloud.consumercenter.common.constant.ErrorMessageEnum;
import com.iwhalecloud.consumercenter.common.error.BusinessException;
import com.iwhalecloud.consumercenter.mvc.mapper.CustomerMapper;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.ai.AIInsightContentDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.ai.AIInsightInputDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.Customer;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.profile.AIInsightResultVO;
import com.iwhalecloud.consumercenter.mvc.service.AIInsightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.Executor;

/**
 * AI洞察服务实现
 */
@Slf4j
@Service
public class AIInsightServiceImpl implements AIInsightService {

    private static final String STATUS_GENERATING = "generating";

    private static final String STATUS_COMPLETED = "completed";

    private static final String STATUS_FAILED = "failed";

    private static final int MAX_ERROR_LENGTH = 500;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private AIInsightDataLoader aiInsightDataLoader;

    @Autowired
    private AIInsightContentGenerator aiInsightContentGenerator;

    @Autowired
    private AIInsightTaskStore aiInsightTaskStore;

    @Autowired
    @Qualifier("aiInsightExecutor")
    private Executor aiInsightExecutor;

    @Value("${spring.ai.openai.api-key:}")
    private String apiKey;

    @Override
    public AIInsightResultVO generateAIInsight(Long customerId) {
        if (!isAiConfigured()) {
            throw new BusinessException(ErrorMessageEnum.AI_INSIGHT_NOT_CONFIGURED);
        }
        AIInsightResultVO existing = aiInsightTaskStore.get(customerId);
        if (existing != null && STATUS_GENERATING.equals(existing.getStatus())) {
            return existing;
        }
        Customer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

        AIInsightResultVO task = new AIInsightResultVO();
        task.setTaskId("AI-" + UUID.randomUUID().toString().replace("-", ""));
        task.setStatus(STATUS_GENERATING);
        task.setCustomerName(customer.getCustomerName());
        task.setTitle(customer.getCustomerName() + " - 行业客户洞察手册");

        if (!aiInsightTaskStore.tryLock(customerId, task.getTaskId())) {
            AIInsightResultVO lockedTask = aiInsightTaskStore.get(customerId);
            if (lockedTask != null) {
                return lockedTask;
            }
            throw new BusinessException(ErrorMessageEnum.AI_INSIGHT_BUSY);
        }

        try {
            aiInsightTaskStore.save(customerId, task);
            aiInsightExecutor.execute(() -> runGeneration(customerId, task));
        } catch (RuntimeException e) {
            log.error("AI洞察任务提交失败, customerId={}", customerId, e);
            aiInsightTaskStore.delete(customerId);
            aiInsightTaskStore.unlock(customerId, task.getTaskId());
            throw new BusinessException(ErrorMessageEnum.AI_INSIGHT_BUSY);
        }
        return task;
    }

    @Override
    public AIInsightResultVO getAIInsightResult(Long customerId, String taskId) {
        AIInsightResultVO stored = aiInsightTaskStore.get(customerId);
        if (stored == null || !taskId.equals(stored.getTaskId())) {
            throw new BusinessException(ErrorMessageEnum.AI_INSIGHT_TASK_NOT_FOUND);
        }
        return stored;
    }

    private void runGeneration(Long customerId, AIInsightResultVO task) {
        long startTime = System.currentTimeMillis();
        try {
            AIInsightInputDTO input = aiInsightDataLoader.load(customerId);
            AIInsightContentDTO content = aiInsightContentGenerator.generate(input);
            task.setIndustryOverview(content.getIndustryOverview());
            task.setCustomerAnalysis(content.getCustomerAnalysis());
            task.setCompetitiveLandscape(content.getCompetitiveLandscape());
            task.setOpportunitiesAndSuggestions(content.getOpportunitiesAndSuggestions());
            task.setRiskAlerts(content.getRiskAlerts());
            task.setStatus(STATUS_COMPLETED);
            task.setGeneratedTime(LocalDateTime.now().format(TIME_FORMATTER));
            aiInsightTaskStore.save(customerId, task);
            log.info("AI洞察生成完成, customerId={}, taskId={}, cost={}ms",
                    customerId, task.getTaskId(), System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("AI洞察生成失败, customerId={}, taskId={}", customerId, task.getTaskId(), e);
            task.setStatus(STATUS_FAILED);
            task.setErrorMessage(truncate(e.getMessage()));
            aiInsightTaskStore.save(customerId, task);
        } finally {
            aiInsightTaskStore.unlock(customerId, task.getTaskId());
        }
    }

    private boolean isAiConfigured() {
        return StringUtils.hasText(apiKey) && !"unset".equals(apiKey);
    }

    private String truncate(String message) {
        if (message == null) {
            return ErrorMessageEnum.AI_INSIGHT_GENERATE_FAILED.getMessage();
        }
        return message.length() > MAX_ERROR_LENGTH ? message.substring(0, MAX_ERROR_LENGTH) : message;
    }
}