package com.iwhalecloud.consumercenter.mvc.service.impl;

import com.iwhalecloud.consumercenter.common.constant.ErrorMessageEnum;
import com.iwhalecloud.consumercenter.common.error.BusinessException;
import com.iwhalecloud.consumercenter.mvc.mapper.CustomerMapper;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.ai.AIInsightContentDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.ai.AIInsightInputDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.Customer;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.profile.AIInsightResultVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * AI洞察服务测试
 */
@ExtendWith(MockitoExtension.class)
class AIInsightServiceImplTest {

    @InjectMocks
    private AIInsightServiceImpl aiInsightService;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private AIInsightDataLoader aiInsightDataLoader;

    @Mock
    private AIInsightContentGenerator aiInsightContentGenerator;

    @Mock
    private AIInsightTaskStore aiInsightTaskStore;

    @Mock
    private Executor aiInsightExecutor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(aiInsightService, "apiKey", "test-key");
    }

    private Customer buildCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCustomerName("测试客户");
        return customer;
    }

    @Test
    @DisplayName("网关未配置时直接抛4102")
    void generateAIInsight_whenApiKeyMissing_throwsNotConfigured() {
        ReflectionTestUtils.setField(aiInsightService, "apiKey", "unset");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> aiInsightService.generateAIInsight(1L));

        assertEquals(ErrorMessageEnum.AI_INSIGHT_NOT_CONFIGURED.getCode(), exception.getCode());
        verifyNoInteractions(customerMapper, aiInsightTaskStore, aiInsightExecutor);
    }

    @Test
    @DisplayName("已有生成中任务时幂等返回同一任务")
    void generateAIInsight_whenGeneratingTaskExists_returnsExistingTask() {
        AIInsightResultVO existing = new AIInsightResultVO();
        existing.setTaskId("AI-existing");
        existing.setStatus("generating");
        existing.setCustomerName("测试客户");
        when(aiInsightTaskStore.get(1L)).thenReturn(existing);

        AIInsightResultVO result = aiInsightService.generateAIInsight(1L);

        assertEquals("AI-existing", result.getTaskId());
        verify(aiInsightExecutor, never()).execute(any());
        verify(aiInsightTaskStore, never()).tryLock(anyLong(), anyString());
    }

    @Test
    @DisplayName("客户不存在时抛业务异常且不创建任务")
    void generateAIInsight_whenCustomerMissing_throwsBusinessException() {
        when(aiInsightTaskStore.get(1L)).thenReturn(null);
        when(customerMapper.selectById(1L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> aiInsightService.generateAIInsight(1L));

        assertEquals("客户不存在", exception.getMessage());
        verify(aiInsightTaskStore, never()).save(anyLong(), any());
        verify(aiInsightExecutor, never()).execute(any());
    }

    @Test
    @DisplayName("正常提交时创建generating任务并交给线程池")
    void generateAIInsight_whenIdle_createsGeneratingTask() {
        when(aiInsightTaskStore.get(1L)).thenReturn(null);
        when(customerMapper.selectById(1L)).thenReturn(buildCustomer());
        when(aiInsightTaskStore.tryLock(eq(1L), anyString())).thenReturn(true);

        AIInsightResultVO result = aiInsightService.generateAIInsight(1L);

        assertTrue(result.getTaskId().startsWith("AI-"));
        assertEquals("generating", result.getStatus());
        assertEquals("测试客户", result.getCustomerName());
        assertEquals("测试客户 - 行业客户洞察手册", result.getTitle());
        verify(aiInsightTaskStore).save(eq(1L), same(result));
        verify(aiInsightExecutor).execute(any(Runnable.class));
    }

    @Test
    @DisplayName("抢锁失败且无任务时抛任务繁忙")
    void generateAIInsight_whenLockBusy_throwsBusy() {
        when(aiInsightTaskStore.get(1L)).thenReturn(null, null);
        when(customerMapper.selectById(1L)).thenReturn(buildCustomer());
        when(aiInsightTaskStore.tryLock(eq(1L), anyString())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> aiInsightService.generateAIInsight(1L));

        assertEquals(ErrorMessageEnum.AI_INSIGHT_BUSY.getCode(), exception.getCode());
        verify(aiInsightExecutor, never()).execute(any());
    }

    @Test
    @DisplayName("后台生成成功时任务状态变为completed")
    void generateAIInsight_whenWorkerSucceeds_marksCompleted() {
        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(aiInsightExecutor).execute(any(Runnable.class));

        AIInsightInputDTO input = new AIInsightInputDTO();
        AIInsightContentDTO content = new AIInsightContentDTO();
        content.setIndustryOverview("行业概况");
        content.setCustomerAnalysis("客户分析");
        content.setCompetitiveLandscape("竞争格局");
        content.setOpportunitiesAndSuggestions("机会与建议");
        content.setRiskAlerts("风险提示");

        when(aiInsightTaskStore.get(1L)).thenReturn(null);
        when(customerMapper.selectById(1L)).thenReturn(buildCustomer());
        when(aiInsightTaskStore.tryLock(eq(1L), anyString())).thenReturn(true);
        when(aiInsightDataLoader.load(1L)).thenReturn(input);
        when(aiInsightContentGenerator.generate(input)).thenReturn(content);

        AIInsightResultVO task = aiInsightService.generateAIInsight(1L);

        assertEquals("completed", task.getStatus());
        assertEquals("行业概况", task.getIndustryOverview());
        assertEquals("风险提示", task.getRiskAlerts());
        assertNotNull(task.getGeneratedTime());
        verify(aiInsightTaskStore, times(2)).save(eq(1L), any());
        verify(aiInsightTaskStore).unlock(1L, task.getTaskId());
    }

    @Test
    @DisplayName("后台生成失败时任务状态变为failed并记录错误")
    void generateAIInsight_whenWorkerFails_marksFailed() {
        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(aiInsightExecutor).execute(any(Runnable.class));

        when(aiInsightTaskStore.get(1L)).thenReturn(null);
        when(customerMapper.selectById(1L)).thenReturn(buildCustomer());
        when(aiInsightTaskStore.tryLock(eq(1L), anyString())).thenReturn(true);
        when(aiInsightDataLoader.load(1L)).thenThrow(new RuntimeException("网关超时"));

        AIInsightResultVO task = aiInsightService.generateAIInsight(1L);

        assertEquals("failed", task.getStatus());
        assertTrue(task.getErrorMessage().contains("网关超时"));
        verify(aiInsightTaskStore).unlock(1L, task.getTaskId());
    }

    @Test
    @DisplayName("查询不存在的任务抛4101")
    void getAIInsightResult_whenTaskMissing_throwsNotFound() {
        when(aiInsightTaskStore.get(1L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> aiInsightService.getAIInsightResult(1L, "AI-missing"));

        assertEquals(ErrorMessageEnum.AI_INSIGHT_TASK_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("taskId不匹配时抛4101")
    void getAIInsightResult_whenTaskIdMismatch_throwsNotFound() {
        AIInsightResultVO stored = new AIInsightResultVO();
        stored.setTaskId("AI-other");
        stored.setStatus("completed");
        when(aiInsightTaskStore.get(1L)).thenReturn(stored);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> aiInsightService.getAIInsightResult(1L, "AI-wanted"));

        assertEquals(ErrorMessageEnum.AI_INSIGHT_TASK_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    @DisplayName("查询已完成任务返回完整结果")
    void getAIInsightResult_whenTaskMatches_returnsStoredResult() {
        AIInsightResultVO stored = new AIInsightResultVO();
        stored.setTaskId("AI-1");
        stored.setStatus("completed");
        stored.setIndustryOverview("行业概况");
        when(aiInsightTaskStore.get(1L)).thenReturn(stored);

        AIInsightResultVO result = aiInsightService.getAIInsightResult(1L, "AI-1");

        assertSame(stored, result);
    }
}