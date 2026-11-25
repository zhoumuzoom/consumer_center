package com.iwhalecloud.consumercenter.mvc.service.ai.tool;

import com.alibaba.fastjson2.JSON;
import com.iwhalecloud.consumercenter.common.result.PageResult;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerQueryDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.customer.CustomerVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.profile.CustomerBranchVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.profile.CustomerOrderVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.profile.CustomerProductVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.profile.CustomerProfileVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.profile.PolicyVO;
import com.iwhalecloud.consumercenter.mvc.service.CustomerProfileService;
import com.iwhalecloud.consumercenter.mvc.service.CustomerService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户画像工具
 */
@Component
public class ProfileQueryTools {

    private static final int MAX_ITEMS = 10;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerProfileService customerProfileService;

    @Tool(name = "getCustomerProfile", description = "按客户名称查询客户360画像摘要，包含基础信息、业务统计、分支机构、历史订单、产品方案、政策动态和决策链数量。用户询问某个客户的具体情况时使用。")
    public String getCustomerProfile(
            @ToolParam(description = "客户名称，尽量使用全称") String customerName) {
        CustomerQueryDTO queryDTO = new CustomerQueryDTO();
        queryDTO.setPage(1);
        queryDTO.setSize(5);
        queryDTO.setKeyword(customerName);
        PageResult<CustomerVO> pageResult = customerService.pageList(queryDTO);
        if (pageResult.getRecords() == null || pageResult.getRecords().isEmpty()) {
            return "未找到客户：" + customerName;
        }
        CustomerVO matched = pageResult.getRecords().get(0);
        CustomerProfileVO profile = customerProfileService.getProfile(matched.getId());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("customerId", matched.getId());
        result.put("customerName", matched.getCustomerName());
        result.put("industry", matched.getIndustry());
        result.put("region", matched.getRegion());
        result.put("scale", matched.getScale());
        result.put("status", matched.getStatusDesc());
        result.put("managerName", matched.getManagerName());

        CustomerProfileVO.BasicInfo basicInfo = profile.getBasicInfo();
        if (basicInfo != null) {
            result.put("customerNameEn", basicInfo.getCustomerNameEn());
        }

        CustomerProfileVO.BusinessInfo businessInfo = profile.getBusinessInfo();
        if (businessInfo != null) {
            Map<String, Object> business = new LinkedHashMap<>();
            business.put("orderCount", businessInfo.getOrderCount());
            business.put("orderAmount", businessInfo.getOrderAmount());
            business.put("productCount", businessInfo.getProductCount());
            business.put("revenueContribution", businessInfo.getRevenueContribution());
            business.put("cooperationYears", businessInfo.getCooperationYears());
            result.put("business", business);
        }

        List<CustomerBranchVO> flattenedBranches = new ArrayList<>();
        flattenBranches(profile.getBranches(), flattenedBranches);
        List<Map<String, Object>> branches = new ArrayList<>();
        for (CustomerBranchVO branch : limit(flattenedBranches)) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("branchName", branch.getBranchName());
            item.put("region", branch.getRegion());
            item.put("branchType", branch.getBranchType());
            branches.add(item);
        }
        result.put("branches", branches);

        List<Map<String, Object>> orders = new ArrayList<>();
        for (CustomerOrderVO order : limit(profile.getOrders())) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("projectName", order.getProjectName());
            item.put("projectType", order.getProjectType());
            item.put("orderAmount", order.getOrderAmount());
            item.put("orderStatus", order.getOrderStatus());
            item.put("contractDate", order.getContractDate() == null ? null : order.getContractDate().format(DATE_FORMATTER));
            orders.add(item);
        }
        result.put("orders", orders);

        List<Map<String, Object>> products = new ArrayList<>();
        for (CustomerProductVO product : limit(profile.getProducts())) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("productName", product.getProductName());
            item.put("productType", product.getProductType());
            item.put("category", product.getCategory());
            item.put("usageStatus", product.getUsageStatus());
            products.add(item);
        }
        result.put("products", products);

        List<Map<String, Object>> policies = new ArrayList<>();
        for (PolicyVO policy : limit(profile.getPolicies())) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("policyTitle", policy.getPolicyTitle());
            item.put("status", policy.getStatus());
            item.put("expireDate", policy.getExpireDate() == null ? null : policy.getExpireDate().format(DATE_FORMATTER));
            policies.add(item);
        }
        result.put("policies", policies);

        result.put("decisionChainCount", profile.getDecisionChains() == null ? 0 : profile.getDecisionChains().size());
        return JSON.toJSONString(result);
    }

    private void flattenBranches(List<CustomerBranchVO> source, List<CustomerBranchVO> target) {
        if (source == null || source.isEmpty()) {
            return;
        }
        for (CustomerBranchVO branch : source) {
            target.add(branch);
            flattenBranches(branch.getChildren(), target);
        }
    }

    private <T> List<T> limit(List<T> source) {
        if (source == null || source.isEmpty()) {
            return new ArrayList<>();
        }
        return source.subList(0, Math.min(source.size(), MAX_ITEMS));
    }
}