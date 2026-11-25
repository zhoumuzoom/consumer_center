package com.iwhalecloud.consumercenter.mvc.service.impl;

import com.iwhalecloud.consumercenter.common.error.BusinessException;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.ai.AIInsightContentDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.ai.AIInsightInputDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.Customer;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerOrder;
import com.iwhalecloud.consumercenter.mvc.service.ai.AiChatFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * AI洞察内容生成器测试
 */
@ExtendWith(MockitoExtension.class)
class AIInsightContentGeneratorTest {

    @InjectMocks
    private AIInsightContentGenerator aiInsightContentGenerator;

    @Mock
    private AiChatFacade aiChatFacade;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(aiInsightContentGenerator, "systemPromptResource",
                new ByteArrayResource("你是行业分析专家".getBytes(StandardCharsets.UTF_8)));
    }

    private AIInsightInputDTO buildInput() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCustomerName("测试客户");
        customer.setIndustry("制造业");
        customer.setRegion("东南亚");

        CustomerOrder order = new CustomerOrder();
        order.setProjectName("数字化项目");
        order.setOrderAmount(new BigDecimal("1200"));

        AIInsightInputDTO input = new AIInsightInputDTO();
        input.setCustomer(customer);
        input.setOrders(Collections.singletonList(order));
        input.setBranches(Collections.emptyList());
        input.setProducts(Collections.emptyList());
        input.setPolicies(Collections.emptyList());
        return input;
    }

    @Test
    @DisplayName("生成时把客户数据带入prompt并返回模型结果")
    void generate_whenFacadeReturnsContent_returnsContent() {
        AIInsightInputDTO input = buildInput();
        AIInsightContentDTO content = fullContent();
        when(aiChatFacade.callForEntity(anyString(), anyString(), eq(AIInsightContentDTO.class)))
                .thenReturn(content);

        AIInsightContentDTO result = aiInsightContentGenerator.generate(input);

        assertSame(content, result);
    }

    @Test
    @DisplayName("模型返回字段不完整时抛业务异常")
    void generate_whenContentIncomplete_throwsBusinessException() {
        AIInsightInputDTO input = buildInput();
        AIInsightContentDTO content = fullContent();
        content.setRiskAlerts("");
        when(aiChatFacade.callForEntity(anyString(), anyString(), eq(AIInsightContentDTO.class)))
                .thenReturn(content);

        assertThrows(BusinessException.class, () -> aiInsightContentGenerator.generate(input));
    }

    private AIInsightContentDTO fullContent() {
        AIInsightContentDTO content = new AIInsightContentDTO();
        content.setIndustryOverview("行业概况");
        content.setCustomerAnalysis("客户分析");
        content.setCompetitiveLandscape("竞争格局");
        content.setOpportunitiesAndSuggestions("机会与建议");
        content.setRiskAlerts("风险提示");
        return content;
    }

    @Test
    @DisplayName("模型返回空结果时抛业务异常")
    void generate_whenFacadeReturnsNull_throwsBusinessException() {
        AIInsightInputDTO input = buildInput();
        when(aiChatFacade.callForEntity(anyString(), anyString(), eq(AIInsightContentDTO.class)))
                .thenReturn(null);

        assertThrows(BusinessException.class, () -> aiInsightContentGenerator.generate(input));
    }

    @Test
    @DisplayName("user prompt包含客户名、行业和订单信息")
    void buildUserPrompt_containsCustomerData() {
        String prompt = aiInsightContentGenerator.buildUserPrompt(buildInput());

        assertTrue(prompt.contains("测试客户"));
        assertTrue(prompt.contains("制造业"));
        assertTrue(prompt.contains("数字化项目"));
    }
}