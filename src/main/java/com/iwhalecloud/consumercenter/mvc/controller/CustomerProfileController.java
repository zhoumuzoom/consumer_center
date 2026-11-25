package com.iwhalecloud.consumercenter.mvc.controller;

import com.iwhalecloud.consumercenter.common.result.Result;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.profile.*;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.profile.*;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.task.TaskVO;
import com.iwhalecloud.consumercenter.mvc.service.AIInsightService;
import com.iwhalecloud.consumercenter.mvc.service.CustomerProfileService;
import com.iwhalecloud.consumercenter.mvc.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户画像
 */
@RestController
@RequestMapping("/api/customer-profiles")
public class CustomerProfileController {

    @Autowired
    private CustomerProfileService customerProfileService;

    @Autowired
    private AIInsightService aiInsightService;

    @Autowired
    private TaskService taskService;

    @GetMapping("/{customerId}")
    public Result<CustomerProfileVO> getProfile(@PathVariable Long customerId) {
        return Result.success(customerProfileService.getProfile(customerId));
    }

    // ==================== 分支管理 ====================

    @GetMapping("/{customerId}/branches")
    public Result<List<CustomerBranchVO>> getBranchTree(@PathVariable Long customerId) {
        return Result.success(customerProfileService.getBranchTree(customerId));
    }

    @PostMapping("/{customerId}/branches")
    public Result<CustomerBranchVO> createBranch(@PathVariable Long customerId,
                                                  @Valid @RequestBody CustomerBranchCreateDTO createDTO) {
        return Result.success(customerProfileService.createBranch(customerId, createDTO));
    }

    @PutMapping("/{customerId}/branches/{branchId}")
    public Result<CustomerBranchVO> updateBranch(@PathVariable Long customerId, @PathVariable Long branchId,
                                                  @Valid @RequestBody CustomerBranchUpdateDTO updateDTO) {
        return Result.success(customerProfileService.updateBranch(customerId, branchId, updateDTO));
    }

    @DeleteMapping("/{customerId}/branches/{branchId}")
    public Result<Void> deleteBranch(@PathVariable Long customerId, @PathVariable Long branchId) {
        customerProfileService.deleteBranch(customerId, branchId);
        return Result.success();
    }

    @GetMapping("/{customerId}/branches/{branchId}/analysis")
    public Result<BranchAnalysisVO> branchAnalysis(@PathVariable Long customerId, @PathVariable Long branchId) {
        return Result.success(customerProfileService.branchAnalysis(customerId, branchId));
    }

    // ==================== 政策动态 ====================

    @GetMapping("/{customerId}/policies")
    public Result<List<PolicyVO>> listPolicies(@PathVariable Long customerId) {
        return Result.success(customerProfileService.listPolicies(customerId));
    }

    @PostMapping("/{customerId}/policies")
    public Result<PolicyVO> createPolicy(@PathVariable Long customerId,
                                          @Valid @RequestBody PolicyCreateDTO createDTO) {
        return Result.success(customerProfileService.createPolicy(customerId, createDTO));
    }

    @DeleteMapping("/{customerId}/policies/{policyId}")
    public Result<Void> deletePolicy(@PathVariable Long customerId, @PathVariable Long policyId) {
        customerProfileService.deletePolicy(customerId, policyId);
        return Result.success();
    }

    // ==================== 历史订单/项目 ====================

    @GetMapping("/{customerId}/orders")
    public Result<List<CustomerOrderVO>> listOrders(@PathVariable Long customerId) {
        return Result.success(customerProfileService.listOrders(customerId));
    }

    @PostMapping("/{customerId}/orders")
    public Result<CustomerOrderVO> createOrder(@PathVariable Long customerId,
                                                @Valid @RequestBody CustomerOrderCreateDTO createDTO) {
        return Result.success(customerProfileService.createOrder(customerId, createDTO));
    }

    @DeleteMapping("/{customerId}/orders/{orderId}")
    public Result<Void> deleteOrder(@PathVariable Long customerId, @PathVariable Long orderId) {
        customerProfileService.deleteOrder(customerId, orderId);
        return Result.success();
    }

    // ==================== 产品与方案 ====================

    @GetMapping("/{customerId}/products")
    public Result<List<CustomerProductVO>> listProducts(@PathVariable Long customerId) {
        return Result.success(customerProfileService.listProducts(customerId));
    }

    @PostMapping("/{customerId}/products")
    public Result<CustomerProductVO> createProduct(@PathVariable Long customerId,
                                                    @Valid @RequestBody CustomerProductCreateDTO createDTO) {
        return Result.success(customerProfileService.createProduct(customerId, createDTO));
    }

    @DeleteMapping("/{customerId}/products/{productId}")
    public Result<Void> deleteProduct(@PathVariable Long customerId, @PathVariable Long productId) {
        customerProfileService.deleteProduct(customerId, productId);
        return Result.success();
    }

    // ==================== 任务调度 ====================

    @PostMapping("/{customerId}/tasks")
    public Result<TaskVO> createTask(@PathVariable Long customerId,
                                      @Valid @RequestBody TaskCreateDTO taskDTO) {
        taskDTO.setCustomerId(customerId);
        return Result.success(taskService.createTask(taskDTO));
    }

    // ==================== AI 洞察 ====================

    @PostMapping("/{customerId}/ai-insight")
    public Result<AIInsightResultVO> generateAIInsight(@PathVariable Long customerId) {
        return Result.success(aiInsightService.generateAIInsight(customerId));
    }

    @GetMapping("/{customerId}/ai-insight/{taskId}")
    public Result<AIInsightResultVO> getAIInsightResult(@PathVariable Long customerId, @PathVariable String taskId) {
        return Result.success(aiInsightService.getAIInsightResult(customerId, taskId));
    }
}
