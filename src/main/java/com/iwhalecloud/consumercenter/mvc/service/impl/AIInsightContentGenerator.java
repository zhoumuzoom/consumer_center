package com.iwhalecloud.consumercenter.mvc.service.impl;

import com.alibaba.fastjson2.JSON;
import com.iwhalecloud.consumercenter.common.constant.ErrorMessageEnum;
import com.iwhalecloud.consumercenter.common.error.BusinessException;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.ai.AIInsightContentDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.ai.AIInsightInputDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.Customer;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerBranch;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerOrder;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerPolicy;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerProduct;
import com.iwhalecloud.consumercenter.mvc.service.ai.AiChatFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AI洞察内容生成
 */
@Component
public class AIInsightContentGenerator {

    private static final int MAX_LIST_SIZE = 20;

    @Autowired
    private AiChatFacade aiChatFacade;

    @Value("classpath:ai/prompts/insight-system.st")
    private Resource systemPromptResource;

    public AIInsightContentDTO generate(AIInsightInputDTO input) {
        AIInsightContentDTO content = aiChatFacade.callForEntity(readSystemPrompt(), buildUserPrompt(input),
                AIInsightContentDTO.class);
        if (content == null || isIncomplete(content)) {
            throw new BusinessException(ErrorMessageEnum.AI_INSIGHT_GENERATE_FAILED);
        }
        return content;
    }

    String buildUserPrompt(AIInsightInputDTO input) {
        Customer customer = input.getCustomer();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("客户名称", customer.getCustomerName());
        data.put("所属行业", customer.getIndustry());
        data.put("所属区域", customer.getRegion());
        data.put("企业规模", customer.getScale());

        List<Map<String, Object>> branches = new ArrayList<>();
        for (CustomerBranch branch : limit(input.getBranches())) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("分支名称", branch.getBranchName());
            item.put("区域", branch.getRegion());
            item.put("类型", branch.getBranchType());
            branches.add(item);
        }
        data.put("分支机构", branches);

        List<Map<String, Object>> orders = new ArrayList<>();
        for (CustomerOrder order : limit(input.getOrders())) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("项目名称", order.getProjectName());
            item.put("项目类型", order.getProjectType());
            item.put("订单金额(万元)", order.getOrderAmount());
            item.put("订单状态", order.getOrderStatus());
            item.put("签约时间", order.getContractDate());
            orders.add(item);
        }
        data.put("历史订单", orders);

        List<Map<String, Object>> products = new ArrayList<>();
        for (CustomerProduct product : limit(input.getProducts())) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("产品名称", product.getProductName());
            item.put("产品类型", product.getProductType());
            item.put("技术方向", product.getCategory());
            item.put("使用状态", product.getUsageStatus());
            item.put("年费", product.getAnnualFee());
            products.add(item);
        }
        data.put("产品方案", products);

        List<Map<String, Object>> policies = new ArrayList<>();
        for (CustomerPolicy policy : limit(input.getPolicies())) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("政策标题", policy.getPolicyTitle());
            item.put("政策类型", policy.getPolicyType());
            item.put("发布机构", policy.getIssuingAuthority());
            item.put("生效时间", policy.getEffectiveDate());
            item.put("到期时间", policy.getExpireDate());
            policies.add(item);
        }
        data.put("有效政策", policies);

        return "请基于以下客户数据生成洞察手册正文：\n" + JSON.toJSONString(data);
    }

    private <T> List<T> limit(List<T> source) {
        if (source == null || source.isEmpty()) {
            return new ArrayList<>();
        }
        return source.subList(0, Math.min(source.size(), MAX_LIST_SIZE));
    }

    private boolean isIncomplete(AIInsightContentDTO content) {
        return !StringUtils.hasText(content.getIndustryOverview())
                || !StringUtils.hasText(content.getCustomerAnalysis())
                || !StringUtils.hasText(content.getCompetitiveLandscape())
                || !StringUtils.hasText(content.getOpportunitiesAndSuggestions())
                || !StringUtils.hasText(content.getRiskAlerts());
    }

    private String readSystemPrompt() {
        try (InputStream inputStream = systemPromptResource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new BusinessException(ErrorMessageEnum.AI_INSIGHT_GENERATE_FAILED);
        }
    }
}