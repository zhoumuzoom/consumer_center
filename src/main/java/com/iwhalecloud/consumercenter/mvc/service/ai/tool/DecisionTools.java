package com.iwhalecloud.consumercenter.mvc.service.ai.tool;

import com.alibaba.fastjson2.JSON;
import com.iwhalecloud.consumercenter.common.result.PageResult;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.decision.DecisionChainQueryDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.decision.DecisionChainVO;
import com.iwhalecloud.consumercenter.mvc.service.DecisionMakerService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 决策链工具
 */
@Component
public class DecisionTools {

    @Autowired
    private DecisionMakerService decisionMakerService;

    @Tool(name = "getDecisionChains", description = "按客户名称、项目名称或决策人岗位查询决策链关键人，返回最多10条。用户询问某客户的关键决策人、决策链、对接人时使用。")
    public String getDecisionChains(
            @ToolParam(description = "客户名称，可为空") String customerName,
            @ToolParam(description = "项目名称，可为空") String projectName,
            @ToolParam(description = "决策人岗位，可为空") String position) {
        DecisionChainQueryDTO queryDTO = new DecisionChainQueryDTO();
        queryDTO.setPage(1);
        queryDTO.setSize(10);
        queryDTO.setCustomerName(customerName);
        queryDTO.setProjectName(projectName);
        queryDTO.setPosition(position);

        PageResult<DecisionChainVO> pageResult = decisionMakerService.pageDecisionChains(queryDTO);
        Map<String, Object> result = new LinkedHashMap<>();
        // 客户名称、岗位是分页后内存过滤，分页总条数不代表过滤结果，只在无此类过滤时给出
        if (!StringUtils.hasText(customerName) && !StringUtils.hasText(position)) {
            result.put("total", pageResult.getTotal() == null ? 0L : pageResult.getTotal());
        }
        List<Map<String, Object>> records = new ArrayList<>();
        if (pageResult.getRecords() != null) {
            for (DecisionChainVO chain : pageResult.getRecords()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("customerName", chain.getCustomerName());
                item.put("projectName", chain.getProjectName());
                item.put("decisionMakerName", chain.getDecisionMakerName());
                item.put("position", chain.getDecisionMakerPosition());
                item.put("decisionStage", chain.getDecisionStage());
                item.put("influenceLevel", chain.getInfluenceLevel());
                records.add(item);
            }
        }
        result.put("returned", records.size());
        result.put("records", records);
        return JSON.toJSONString(result);
    }
}