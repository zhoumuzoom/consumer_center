package com.iwhalecloud.consumercenter.mvc.service.ai.tool;

import com.alibaba.fastjson2.JSON;
import com.iwhalecloud.consumercenter.common.result.PageResult;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerQueryDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.customer.CustomerVO;
import com.iwhalecloud.consumercenter.mvc.service.CustomerService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户查询工具
 */
@Component
public class CustomerQueryTools {

    @Autowired
    private CustomerService customerService;

    @Tool(name = "searchCustomers", description = "按客户名称关键字、行业、区域、企业规模、看管状态查询客户列表，返回最多10条客户摘要。用户询问客户名单、某类客户有哪些时使用。")
    public String searchCustomers(
            @ToolParam(description = "客户名称关键字，可为空") String keyword,
            @ToolParam(description = "所属行业，可为空") String industry,
            @ToolParam(description = "所属区域，可为空") String region,
            @ToolParam(description = "企业规模，可为空，取值：大型/中型/小型") String scale,
            @ToolParam(description = "看管状态，可为空，0未看管 1已看管 2已剔除") Integer status) {
        CustomerQueryDTO queryDTO = new CustomerQueryDTO();
        queryDTO.setPage(1);
        queryDTO.setSize(10);
        queryDTO.setKeyword(keyword);
        queryDTO.setIndustry(industry);
        queryDTO.setRegion(region);
        queryDTO.setScale(scale);
        queryDTO.setStatus(status);

        PageResult<CustomerVO> pageResult = customerService.pageList(queryDTO);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", pageResult.getTotal() == null ? 0L : pageResult.getTotal());
        List<Map<String, Object>> records = new ArrayList<>();
        if (pageResult.getRecords() != null) {
            for (CustomerVO customer : pageResult.getRecords()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("customerId", customer.getId());
                item.put("customerName", customer.getCustomerName());
                item.put("industry", customer.getIndustry());
                item.put("region", customer.getRegion());
                item.put("scale", customer.getScale());
                item.put("status", customer.getStatusDesc());
                item.put("managerName", customer.getManagerName());
                records.add(item);
            }
        }
        result.put("records", records);
        return JSON.toJSONString(result);
    }
}