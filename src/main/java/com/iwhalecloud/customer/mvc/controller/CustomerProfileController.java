package com.iwhalecloud.customer.mvc.controller;

import com.iwhalecloud.customer.common.result.Result;
import com.iwhalecloud.customer.mvc.pojo.dto.profile.*;
import com.iwhalecloud.customer.mvc.pojo.vo.profile.*;
import com.iwhalecloud.customer.mvc.pojo.vo.task.TaskVO;
import com.iwhalecloud.customer.mvc.service.CustomerProfileService;
import com.iwhalecloud.customer.mvc.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户画像
 */
@Tag(name = "客户画像", description = "客户360度画像、分支管理、政策动态、历史订单、产品方案、AI洞察")
@RestController
@RequestMapping("/api/customer-profiles")
public class CustomerProfileController {

    @Autowired
    private CustomerProfileService customerProfileService;

    @Autowired
    private TaskService taskService;

    @Operation(summary = "获取客户360度画像")
    @GetMapping("/{customerId}")
    public Result<CustomerProfileVO> getProfile(@PathVariable Long customerId) {
        return Result.success(customerProfileService.getProfile(customerId));
    }

    // ==================== 分支管理 ====================

    @Operation(summary = "客户分支树查询")
    @GetMapping("/{customerId}/branches")
    public Result<List<CustomerBranchVO>> getBranchTree(@PathVariable Long customerId) {
        return Result.success(customerProfileService.getBranchTree(customerId));
    }

    @Operation(summary = "新增客户分支")
    @PostMapping("/{customerId}/branches")
    public Result<CustomerBranchVO> createBranch(@PathVariable Long customerId,
                                                  @Valid @RequestBody CustomerBranchCreateDTO createDTO) {
        return Result.success(customerProfileService.createBranch(customerId, createDTO));
    }

    @Operation(summary = "修改客户分支")
    @PutMapping("/{customerId}/branches/{branchId}")
    public Result<CustomerBranchVO> updateBranch(@PathVariable Long customerId, @PathVariable Long branchId,
                                                  @Valid @RequestBody CustomerBranchUpdateDTO updateDTO) {
        return Result.success(customerProfileService.updateBranch(customerId, branchId, updateDTO));
    }

    @Operation(summary = "删除客户分支")
    @DeleteMapping("/{customerId}/branches/{branchId}")
    public Result<Void> deleteBranch(@PathVariable Long customerId, @PathVariable Long branchId) {
        customerProfileService.deleteBranch(customerId, branchId);
        return Result.success();
    }

    @Operation(summary = "客户分支业务穿透分析")
    @GetMapping("/{customerId}/branches/{branchId}/analysis")
    public Result<BranchAnalysisVO> branchAnalysis(@PathVariable Long customerId, @PathVariable Long branchId) {
        return Result.success(customerProfileService.branchAnalysis(customerId, branchId));
    }

    // ==================== 政策动态 ====================

    @Operation(summary = "查询政策动态列表")
    @GetMapping("/{customerId}/policies")
    public Result<List<PolicyVO>> listPolicies(@PathVariable Long customerId) {
        return Result.success(customerProfileService.listPolicies(customerId));
    }

    @Operation(summary = "新增政策动态")
    @PostMapping("/{customerId}/policies")
    public Result<PolicyVO> createPolicy(@PathVariable Long customerId,
                                          @Valid @RequestBody PolicyCreateDTO createDTO) {
        return Result.success(customerProfileService.createPolicy(customerId, createDTO));
    }

    @Operation(summary = "删除政策动态")
    @DeleteMapping("/{customerId}/policies/{policyId}")
    public Result<Void> deletePolicy(@PathVariable Long customerId, @PathVariable Long policyId) {
        customerProfileService.deletePolicy(customerId, policyId);
        return Result.success();
    }

    // ==================== 历史订单/项目 ====================

    @Operation(summary = "查询历史订单/项目列表")
    @GetMapping("/{customerId}/orders")
    public Result<List<CustomerOrderVO>> listOrders(@PathVariable Long customerId) {
        return Result.success(customerProfileService.listOrders(customerId));
    }

    @Operation(summary = "新增历史订单/项目")
    @PostMapping("/{customerId}/orders")
    public Result<CustomerOrderVO> createOrder(@PathVariable Long customerId,
                                                @Valid @RequestBody CustomerOrderCreateDTO createDTO) {
        return Result.success(customerProfileService.createOrder(customerId, createDTO));
    }

    @Operation(summary = "删除历史订单/项目")
    @DeleteMapping("/{customerId}/orders/{orderId}")
    public Result<Void> deleteOrder(@PathVariable Long customerId, @PathVariable Long orderId) {
        customerProfileService.deleteOrder(customerId, orderId);
        return Result.success();
    }

    // ==================== 产品与方案 ====================

    @Operation(summary = "查询产品与方案列表")
    @GetMapping("/{customerId}/products")
    public Result<List<CustomerProductVO>> listProducts(@PathVariable Long customerId) {
        return Result.success(customerProfileService.listProducts(customerId));
    }

    @Operation(summary = "新增产品/方案")
    @PostMapping("/{customerId}/products")
    public Result<CustomerProductVO> createProduct(@PathVariable Long customerId,
                                                    @Valid @RequestBody CustomerProductCreateDTO createDTO) {
        return Result.success(customerProfileService.createProduct(customerId, createDTO));
    }

    @Operation(summary = "删除产品/方案")
    @DeleteMapping("/{customerId}/products/{productId}")
    public Result<Void> deleteProduct(@PathVariable Long customerId, @PathVariable Long productId) {
        customerProfileService.deleteProduct(customerId, productId);
        return Result.success();
    }

    // ==================== 任务调度 ====================

    @Operation(summary = "画像页关联营销任务发起")
    @PostMapping("/{customerId}/tasks")
    public Result<TaskVO> createTask(@PathVariable Long customerId,
                                      @Valid @RequestBody TaskCreateDTO taskDTO) {
        taskDTO.setCustomerId(customerId);
        return Result.success(taskService.createTask(taskDTO));
    }

    // ==================== AI 洞察 ====================

    @Operation(summary = "AI生成行业客户洞察手册")
    @PostMapping("/{customerId}/ai-insight")
    public Result<AIInsightResultVO> generateAIInsight(@PathVariable Long customerId) {
        return Result.success(customerProfileService.generateAIInsight(customerId));
    }

    @Operation(summary = "获取AI洞察结果")
    @GetMapping("/{customerId}/ai-insight/{taskId}")
    public Result<AIInsightResultVO> getAIInsightResult(@PathVariable Long customerId, @PathVariable String taskId) {
        return Result.success(customerProfileService.getAIInsightResult(customerId, taskId));
    }
}
