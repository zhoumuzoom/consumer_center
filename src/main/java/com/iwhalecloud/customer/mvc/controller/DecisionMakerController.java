package com.iwhalecloud.customer.mvc.controller;

import com.iwhalecloud.customer.common.result.PageResult;
import com.iwhalecloud.customer.common.result.Result;
import com.iwhalecloud.customer.mvc.pojo.dto.decision.*;
import com.iwhalecloud.customer.mvc.pojo.vo.decision.DecisionChainVO;
import com.iwhalecloud.customer.mvc.pojo.vo.decision.DecisionMakerVO;
import com.iwhalecloud.customer.mvc.service.DecisionMakerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 决策关键人管理
 */
@Tag(name = "决策关键人管理", description = "决策人档案及决策链管理")
@RestController
@RequestMapping("/api")
public class DecisionMakerController {

    @Autowired
    private DecisionMakerService decisionMakerService;

    @Operation(summary = "决策链信息查询")
    @GetMapping("/decision-chains")
    public Result<PageResult<DecisionChainVO>> pageDecisionChains(DecisionChainQueryDTO queryDTO) {
        return Result.success(decisionMakerService.pageDecisionChains(queryDTO));
    }

    @Operation(summary = "决策链详情查询")
    @GetMapping("/decision-chains/{id}")
    public Result<DecisionChainVO> getDecisionChainDetail(@PathVariable Long id) {
        return Result.success(decisionMakerService.getDecisionChainDetail(id));
    }

    @Operation(summary = "新增决策人")
    @PostMapping("/decision-makers")
    public Result<DecisionMakerVO> createDecisionMaker(@Valid @RequestBody DecisionMakerCreateDTO createDTO) {
        return Result.success(decisionMakerService.createDecisionMaker(createDTO));
    }

    @Operation(summary = "修改决策人")
    @PutMapping("/decision-makers/{id}")
    public Result<DecisionMakerVO> updateDecisionMaker(@PathVariable Long id, @Valid @RequestBody DecisionMakerUpdateDTO updateDTO) {
        updateDTO.setId(id);
        return Result.success(decisionMakerService.updateDecisionMaker(updateDTO));
    }

    @Operation(summary = "删除决策人")
    @DeleteMapping("/decision-makers/{id}")
    public Result<Void> deleteDecisionMaker(@PathVariable Long id) {
        decisionMakerService.deleteDecisionMaker(id);
        return Result.success();
    }

    @Operation(summary = "建立决策链关系")
    @PostMapping("/decision-chains")
    public Result<DecisionChainVO> createDecisionChain(@Valid @RequestBody DecisionChainCreateDTO createDTO) {
        return Result.success(decisionMakerService.createDecisionChain(createDTO));
    }

    @Operation(summary = "更新决策链关系")
    @PutMapping("/decision-chains/{id}")
    public Result<DecisionChainVO> updateDecisionChain(@PathVariable Long id, @Valid @RequestBody DecisionChainUpdateDTO updateDTO) {
        updateDTO.setId(id);
        return Result.success(decisionMakerService.updateDecisionChain(updateDTO));
    }

    @Operation(summary = "删除决策链关系")
    @DeleteMapping("/decision-chains/{id}")
    public Result<Void> deleteDecisionChain(@PathVariable Long id) {
        decisionMakerService.deleteDecisionChain(id);
        return Result.success();
    }

    @Operation(summary = "决策链数据归档")
    @PostMapping("/decision-chains/{id}/archive")
    public Result<Void> archiveDecisionChain(@PathVariable Long id) {
        decisionMakerService.archiveDecisionChain(id);
        return Result.success();
    }
}
