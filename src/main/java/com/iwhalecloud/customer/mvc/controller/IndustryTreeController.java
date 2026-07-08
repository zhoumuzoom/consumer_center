package com.iwhalecloud.customer.mvc.controller;

import com.iwhalecloud.customer.common.result.Result;
import com.iwhalecloud.customer.mvc.pojo.dto.industry.BatchTagDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.industry.CustomerIndustryTagDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.industry.IndustryTreeNodeCreateDTO;
import com.iwhalecloud.customer.mvc.pojo.vo.industry.CustomerIndustryTagVO;
import com.iwhalecloud.customer.mvc.pojo.vo.industry.IndustryAggregationVO;
import com.iwhalecloud.customer.mvc.pojo.vo.industry.IndustryTreeNodeVO;
import com.iwhalecloud.customer.mvc.pojo.vo.industry.ResourceAnalysisVO;
import com.iwhalecloud.customer.mvc.service.IndustryTreeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 行业树管理
 */
@Tag(name = "行业树管理", description = "行业树维护、客户标签管理、穿透分析")
@RestController
@RequestMapping("/api/industry-tree")
public class IndustryTreeController {

    @Autowired
    private IndustryTreeService industryTreeService;

    @Operation(summary = "获取行业树结构")
    @GetMapping
    public Result<List<IndustryTreeNodeVO>> getTree() {
        return Result.success(industryTreeService.getTree());
    }

    @Operation(summary = "行业树节点聚合数据")
    @GetMapping("/{nodeId}/aggregation")
    public Result<IndustryAggregationVO> getNodeAggregation(@PathVariable Long nodeId) {
        return Result.success(industryTreeService.getNodeAggregation(nodeId));
    }

    @Operation(summary = "行业树节点下钻")
    @GetMapping("/{nodeId}/drill-down")
    public Result<List<IndustryTreeNodeVO>> drillDown(@PathVariable Long nodeId) {
        return Result.success(industryTreeService.drillDown(nodeId));
    }

    @Operation(summary = "新增行业节点")
    @PostMapping("/nodes")
    public Result<IndustryTreeNodeVO> createNode(@Valid @RequestBody IndustryTreeNodeCreateDTO createDTO) {
        return Result.success(industryTreeService.createNode(createDTO));
    }

    @Operation(summary = "设置客户行业标签")
    @PostMapping("/customers/{customerId}/tags")
    public Result<CustomerIndustryTagVO> setCustomerTag(@PathVariable Long customerId, @Valid @RequestBody CustomerIndustryTagDTO tagDTO) {
        return Result.success(industryTreeService.setCustomerTag(customerId, tagDTO));
    }

    @Operation(summary = "批量设置客户标签")
    @PostMapping("/customers/tags/batch")
    public Result<Void> batchSetTags(@Valid @RequestBody BatchTagDTO batchDTO) {
        industryTreeService.batchSetTags(batchDTO);
        return Result.success();
    }

    @Operation(summary = "修改客户行业标签")
    @PutMapping("/customers/{customerId}/tags/{tagId}")
    public Result<CustomerIndustryTagVO> updateCustomerTag(@PathVariable Long customerId, @PathVariable Long tagId, @Valid @RequestBody CustomerIndustryTagDTO tagDTO) {
        return Result.success(industryTreeService.updateCustomerTag(customerId, tagId, tagDTO));
    }

    @Operation(summary = "删除客户行业标签")
    @DeleteMapping("/customers/{customerId}/tags/{tagId}")
    public Result<Void> deleteCustomerTag(@PathVariable Long customerId, @PathVariable Long tagId) {
        industryTreeService.deleteCustomerTag(customerId, tagId);
        return Result.success();
    }

    @Operation(summary = "行业树节点资源穿透分析")
    @GetMapping("/{nodeId}/resource-analysis")
    public Result<ResourceAnalysisVO> resourceAnalysis(@PathVariable Long nodeId) {
        return Result.success(industryTreeService.resourceAnalysis(nodeId));
    }
}
