package com.iwhalecloud.consumercenter.mvc.controller;

import com.iwhalecloud.consumercenter.common.result.Result;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.industry.BatchTagDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.industry.CustomerIndustryTagDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.industry.IndustryTreeNodeCreateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.industry.CustomerIndustryTagVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.industry.IndustryAggregationVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.industry.IndustryTreeNodeVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.industry.ResourceAnalysisVO;
import com.iwhalecloud.consumercenter.mvc.service.IndustryTreeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 行业树管理
 */
@RestController
@RequestMapping("/api/industry-tree")
public class IndustryTreeController {

    @Autowired
    private IndustryTreeService industryTreeService;

    @GetMapping
    public Result<List<IndustryTreeNodeVO>> getTree() {
        return Result.success(industryTreeService.getTree());
    }

    @GetMapping("/{nodeId}/aggregation")
    public Result<IndustryAggregationVO> getNodeAggregation(@PathVariable Long nodeId) {
        return Result.success(industryTreeService.getNodeAggregation(nodeId));
    }

    @GetMapping("/{nodeId}/drill-down")
    public Result<List<IndustryTreeNodeVO>> drillDown(@PathVariable Long nodeId) {
        return Result.success(industryTreeService.drillDown(nodeId));
    }

    @PostMapping("/nodes")
    public Result<IndustryTreeNodeVO> createNode(@Valid @RequestBody IndustryTreeNodeCreateDTO createDTO) {
        return Result.success(industryTreeService.createNode(createDTO));
    }

    @PostMapping("/customers/{customerId}/tags")
    public Result<CustomerIndustryTagVO> setCustomerTag(@PathVariable Long customerId, @Valid @RequestBody CustomerIndustryTagDTO tagDTO) {
        return Result.success(industryTreeService.setCustomerTag(customerId, tagDTO));
    }

    @PostMapping("/customers/tags/batch")
    public Result<Void> batchSetTags(@Valid @RequestBody BatchTagDTO batchDTO) {
        industryTreeService.batchSetTags(batchDTO);
        return Result.success();
    }

    @PutMapping("/customers/{customerId}/tags/{tagId}")
    public Result<CustomerIndustryTagVO> updateCustomerTag(@PathVariable Long customerId, @PathVariable Long tagId, @Valid @RequestBody CustomerIndustryTagDTO tagDTO) {
        return Result.success(industryTreeService.updateCustomerTag(customerId, tagId, tagDTO));
    }

    @DeleteMapping("/customers/{customerId}/tags/{tagId}")
    public Result<Void> deleteCustomerTag(@PathVariable Long customerId, @PathVariable Long tagId) {
        industryTreeService.deleteCustomerTag(customerId, tagId);
        return Result.success();
    }

    @GetMapping("/{nodeId}/resource-analysis")
    public Result<ResourceAnalysisVO> resourceAnalysis(@PathVariable Long nodeId) {
        return Result.success(industryTreeService.resourceAnalysis(nodeId));
    }
}
