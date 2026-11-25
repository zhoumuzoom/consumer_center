package com.iwhalecloud.consumercenter.mvc.controller;

import com.iwhalecloud.consumercenter.common.result.PageResult;
import com.iwhalecloud.consumercenter.common.result.Result;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.decision.*;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.decision.DecisionChainVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.decision.DecisionMakerVO;
import com.iwhalecloud.consumercenter.mvc.service.DecisionMakerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 决策关键人管理
 */
@RestController
@RequestMapping("/api")
public class DecisionMakerController {

    @Autowired
    private DecisionMakerService decisionMakerService;

    @GetMapping("/decision-chains")
    public Result<PageResult<DecisionChainVO>> pageDecisionChains(DecisionChainQueryDTO queryDTO) {
        return Result.success(decisionMakerService.pageDecisionChains(queryDTO));
    }

    @GetMapping("/decision-chains/{id}")
    public Result<DecisionChainVO> getDecisionChainDetail(@PathVariable Long id) {
        return Result.success(decisionMakerService.getDecisionChainDetail(id));
    }

    @PostMapping("/decision-makers")
    public Result<DecisionMakerVO> createDecisionMaker(@Valid @RequestBody DecisionMakerCreateDTO createDTO) {
        return Result.success(decisionMakerService.createDecisionMaker(createDTO));
    }

    @PutMapping("/decision-makers/{id}")
    public Result<DecisionMakerVO> updateDecisionMaker(@PathVariable Long id, @Valid @RequestBody DecisionMakerUpdateDTO updateDTO) {
        updateDTO.setId(id);
        return Result.success(decisionMakerService.updateDecisionMaker(updateDTO));
    }

    @DeleteMapping("/decision-makers/{id}")
    public Result<Void> deleteDecisionMaker(@PathVariable Long id) {
        decisionMakerService.deleteDecisionMaker(id);
        return Result.success();
    }

    @PostMapping("/decision-chains")
    public Result<DecisionChainVO> createDecisionChain(@Valid @RequestBody DecisionChainCreateDTO createDTO) {
        return Result.success(decisionMakerService.createDecisionChain(createDTO));
    }

    @PutMapping("/decision-chains/{id}")
    public Result<DecisionChainVO> updateDecisionChain(@PathVariable Long id, @Valid @RequestBody DecisionChainUpdateDTO updateDTO) {
        updateDTO.setId(id);
        return Result.success(decisionMakerService.updateDecisionChain(updateDTO));
    }

    @DeleteMapping("/decision-chains/{id}")
    public Result<Void> deleteDecisionChain(@PathVariable Long id) {
        decisionMakerService.deleteDecisionChain(id);
        return Result.success();
    }

    @PostMapping("/decision-chains/{id}/archive")
    public Result<Void> archiveDecisionChain(@PathVariable Long id) {
        decisionMakerService.archiveDecisionChain(id);
        return Result.success();
    }
}
