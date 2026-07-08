package com.iwhalecloud.customer.mvc.service;

import com.iwhalecloud.customer.mvc.pojo.dto.profile.*;
import com.iwhalecloud.customer.mvc.pojo.vo.profile.*;

import java.util.List;

/**
 * 客户画像服务
 */
public interface CustomerProfileService {

    /** 获取客户360度画像 */
    CustomerProfileVO getProfile(Long customerId);

    /** 获取客户分支树 */
    List<CustomerBranchVO> getBranchTree(Long customerId);

    /** 新增客户分支 */
    CustomerBranchVO createBranch(Long customerId, CustomerBranchCreateDTO createDTO);

    /** 修改客户分支 */
    CustomerBranchVO updateBranch(Long customerId, Long branchId, CustomerBranchUpdateDTO updateDTO);

    /** 删除客户分支 */
    void deleteBranch(Long customerId, Long branchId);

    /** 分支业务穿透分析 */
    BranchAnalysisVO branchAnalysis(Long customerId, Long branchId);

    /** 发起AI洞察 */
    AIInsightResultVO generateAIInsight(Long customerId);

    /** 获取AI洞察结果 */
    AIInsightResultVO getAIInsightResult(Long customerId, String taskId);

    // ---- 政策动态 CRUD ----
    List<PolicyVO> listPolicies(Long customerId);
    PolicyVO createPolicy(Long customerId, PolicyCreateDTO createDTO);
    void deletePolicy(Long customerId, Long policyId);

    // ---- 历史订单/项目 CRUD ----
    List<CustomerOrderVO> listOrders(Long customerId);
    CustomerOrderVO createOrder(Long customerId, CustomerOrderCreateDTO createDTO);
    void deleteOrder(Long customerId, Long orderId);

    // ---- 产品与方案 CRUD ----
    List<CustomerProductVO> listProducts(Long customerId);
    CustomerProductVO createProduct(Long customerId, CustomerProductCreateDTO createDTO);
    void deleteProduct(Long customerId, Long productId);
}
