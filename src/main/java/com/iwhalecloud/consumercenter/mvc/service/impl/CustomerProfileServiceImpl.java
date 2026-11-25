package com.iwhalecloud.consumercenter.mvc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iwhalecloud.consumercenter.mvc.mapper.*;
import com.iwhalecloud.consumercenter.mvc.pojo.convert.CustomerBranchConvert;
import com.iwhalecloud.consumercenter.mvc.pojo.convert.CustomerOrderConvert;
import com.iwhalecloud.consumercenter.mvc.pojo.convert.CustomerPolicyConvert;
import com.iwhalecloud.consumercenter.mvc.pojo.convert.CustomerProductConvert;
import com.iwhalecloud.consumercenter.mvc.pojo.convert.CustomerProfileConvert;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.profile.*;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.*;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.profile.*;
import com.iwhalecloud.consumercenter.mvc.service.CustomerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 客户画像服务实现
 */
@Service
public class CustomerProfileServiceImpl implements CustomerProfileService {

    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private CustomerBranchMapper customerBranchMapper;
    @Autowired
    private CustomerInteractionMapper customerInteractionMapper;
    @Autowired
    private DecisionChainMapper decisionChainMapper;
    @Autowired
    private DecisionMakerMapper decisionMakerMapper;
    @Autowired
    private CustomerTreeNodeMapper customerTreeNodeMapper;
    @Autowired
    private CustomerPolicyMapper customerPolicyMapper;
    @Autowired
    private CustomerOrderMapper customerOrderMapper;
    @Autowired
    private CustomerProductMapper customerProductMapper;

    @Override
    public CustomerProfileVO getProfile(Long customerId) {
        Customer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            throw new RuntimeException("客户不存在");
        }

        CustomerProfileVO profile = new CustomerProfileVO();

        // ---- 基础信息 ----
        CustomerProfileVO.BasicInfo basicInfo = CustomerProfileConvert.entity2basicInfo(customer);
        profile.setBasicInfo(basicInfo);

        // ---- 看管信息 ----
        CustomerProfileVO.ManagementInfo managementInfo = new CustomerProfileVO.ManagementInfo();
        managementInfo.setStatusDesc(getStatusDesc(customer.getStatus() != null ? customer.getStatus() : (byte) 0));
        managementInfo.setManagerName(customer.getManagerName());
        managementInfo.setJoinTime(customer.getJoinTime() != null ?
                customer.getJoinTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null);
        profile.setManagementInfo(managementInfo);

        // ---- 业务数据（从订单/产品表聚合） ----
        List<CustomerOrder> orders = customerOrderMapper.selectList(
                new LambdaQueryWrapper<CustomerOrder>().eq(CustomerOrder::getCustomerId, customerId));
        List<CustomerProduct> products = customerProductMapper.selectList(
                new LambdaQueryWrapper<CustomerProduct>().eq(CustomerProduct::getCustomerId, customerId));

        CustomerProfileVO.BusinessInfo businessInfo = new CustomerProfileVO.BusinessInfo();
        // 招标数据：从信息化类订单统计
        long itBidCount = orders.stream()
                .filter(o -> "信息化".equals(o.getProjectType()))
                .count();
        BigDecimal totalBidAmount = orders.stream()
                .map(CustomerOrder::getOrderAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        businessInfo.setBidCount(orders.size());
        businessInfo.setBidAmount(totalBidAmount);
        businessInfo.setItBidCount((int) itBidCount);
        businessInfo.setOrderCount(orders.size());
        businessInfo.setOrderAmount(totalBidAmount);
        // 收入贡献 = 已交付/已验收订单金额之和
        long revenue = orders.stream()
                .filter(o -> "已交付".equals(o.getOrderStatus()) || "已验收".equals(o.getOrderStatus()))
                .map(CustomerOrder::getOrderAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .longValue();
        businessInfo.setRevenueContribution(revenue);
        // 合作时长
        if (customer.getJoinTime() != null) {
            businessInfo.setCooperationYears((int) java.time.temporal.ChronoUnit.YEARS
                    .between(customer.getJoinTime(), LocalDateTime.now()) + 1);
        } else {
            businessInfo.setCooperationYears(0);
        }
        businessInfo.setProductCount(products.size());
        profile.setBusinessInfo(businessInfo);

        // ---- 分支树 ----
        List<CustomerBranch> rootBranches = customerBranchMapper.selectList(
                new LambdaQueryWrapper<CustomerBranch>()
                        .eq(CustomerBranch::getCustomerId, customerId)
                        .eq(CustomerBranch::getParentBranchId, 0));
        profile.setBranches(rootBranches.stream()
                .map(b -> buildBranchTree(b, customerId))
                .collect(Collectors.toList()));

        // ---- 交互记录 ----
        List<CustomerInteraction> interactions = customerInteractionMapper.selectList(
                new LambdaQueryWrapper<CustomerInteraction>()
                        .eq(CustomerInteraction::getCustomerId, customerId)
                        .orderByDesc(CustomerInteraction::getInteractionTime));
        profile.setInteractionRecords(interactions.stream()
                .map(i -> {
                    CustomerProfileVO.InteractionRecord record = new CustomerProfileVO.InteractionRecord();
                    record.setId(i.getId());
                    record.setType(i.getInteractionType());
                    record.setContent(i.getInteractionContent());
                    record.setParticipant(i.getParticipant());
                    record.setTime(i.getInteractionTime() != null ?
                            i.getInteractionTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : null);
                    record.setResult(i.getResult());
                    return record;
                })
                .collect(Collectors.toList()));

        // ---- 决策链 ----
        List<DecisionChain> chains = decisionChainMapper.selectList(
                new LambdaQueryWrapper<DecisionChain>()
                        .eq(DecisionChain::getCustomerId, customerId)
                        .orderByAsc(DecisionChain::getSortOrder));
        profile.setDecisionChains(chains.stream()
                .map(c -> {
                    com.iwhalecloud.consumercenter.mvc.pojo.vo.decision.DecisionChainVO vo =
                            new com.iwhalecloud.consumercenter.mvc.pojo.vo.decision.DecisionChainVO();
                    vo.setId(c.getId());
                    vo.setCustomerId(c.getCustomerId());
                    vo.setProjectName(c.getProjectName());
                    vo.setDecisionStage(c.getDecisionStage());
                    vo.setInfluenceLevel(c.getInfluenceLevel());
                    vo.setRemark(c.getRemark());
                    DecisionMaker maker = decisionMakerMapper.selectById(c.getDecisionMakerId());
                    if (maker != null) {
                        vo.setDecisionMakerName(maker.getName());
                        vo.setDecisionMakerPosition(maker.getPosition());
                    }
                    return vo;
                })
                .collect(Collectors.toList()));

        // ---- 政策动态 ----
        profile.setPolicies(listPolicies(customerId));

        // ---- 历史订单/项目 ----
        profile.setOrders(orders.stream().map(CustomerOrderConvert::entity2vo).collect(Collectors.toList()));

        // ---- 产品与方案 ----
        profile.setProducts(products.stream().map(CustomerProductConvert::entity2vo).collect(Collectors.toList()));

        return profile;
    }

    // ==================== 分支管理 ====================

    @Override
    public List<CustomerBranchVO> getBranchTree(Long customerId) {
        List<CustomerBranch> rootBranches = customerBranchMapper.selectList(
                new LambdaQueryWrapper<CustomerBranch>()
                        .eq(CustomerBranch::getCustomerId, customerId)
                        .eq(CustomerBranch::getParentBranchId, 0));
        return rootBranches.stream()
                .map(b -> buildBranchTree(b, customerId))
                .collect(Collectors.toList());
    }

    @Override
    public CustomerBranchVO createBranch(Long customerId, CustomerBranchCreateDTO createDTO) {
        CustomerBranch branch = CustomerBranchConvert.dto2entity(createDTO);
        branch.setCustomerId(customerId);
        if (branch.getBranchType() == null) branch.setBranchType("总部");
        customerBranchMapper.insert(branch);
        return CustomerBranchConvert.entity2vo(branch);
    }

    @Override
    public CustomerBranchVO updateBranch(Long customerId, Long branchId, CustomerBranchUpdateDTO updateDTO) {
        CustomerBranch branch = customerBranchMapper.selectById(branchId);
        if (branch == null || !branch.getCustomerId().equals(customerId)) {
            throw new RuntimeException("分支不存在");
        }
        CustomerBranchConvert.dto2entity(updateDTO, branch);
        customerBranchMapper.updateById(branch);
        return CustomerBranchConvert.entity2vo(branch);
    }

    @Override
    public void deleteBranch(Long customerId, Long branchId) {
        customerBranchMapper.deleteById(branchId);
    }

    @Override
    public BranchAnalysisVO branchAnalysis(Long customerId, Long branchId) {
        CustomerBranch branch = customerBranchMapper.selectById(branchId);
        if (branch == null) {
            throw new RuntimeException("分支不存在");
        }
        // 统计该分支下的子分支数量
        long childCount = customerBranchMapper.selectCount(
                new LambdaQueryWrapper<CustomerBranch>()
                        .eq(CustomerBranch::getCustomerId, customerId)
                        .eq(CustomerBranch::getParentBranchId, branchId));

        BranchAnalysisVO analysis = new BranchAnalysisVO();
        analysis.setBranchId(branchId);
        analysis.setBranchName(branch.getBranchName());
        analysis.setRegion(branch.getRegion());
        analysis.setEmployeeCount(branch.getEmployeeCount() != null ? branch.getEmployeeCount() : 0);
        analysis.setBusinessContribution(0L);
        analysis.setResourceInvestment(0L);
        analysis.setContributionRate(0.0);
        analysis.setRoi(0.0);
        return analysis;
    }

    // ==================== 政策动态 CRUD ====================

    @Override
    public List<PolicyVO> listPolicies(Long customerId) {
        return customerPolicyMapper.selectList(
                new LambdaQueryWrapper<CustomerPolicy>()
                        .eq(CustomerPolicy::getCustomerId, customerId)
                        .orderByDesc(CustomerPolicy::getPublishDate))
                .stream().map(CustomerPolicyConvert::entity2vo).collect(Collectors.toList());
    }

    @Override
    public PolicyVO createPolicy(Long customerId, PolicyCreateDTO createDTO) {
        CustomerPolicy policy = CustomerPolicyConvert.dto2entity(createDTO);
        policy.setCustomerId(customerId);
        policy.setStatus("有效");
        customerPolicyMapper.insert(policy);
        return CustomerPolicyConvert.entity2vo(policy);
    }

    @Override
    public void deletePolicy(Long customerId, Long policyId) {
        customerPolicyMapper.deleteById(policyId);
    }

    // ==================== 历史订单/项目 CRUD ====================

    @Override
    public List<CustomerOrderVO> listOrders(Long customerId) {
        return customerOrderMapper.selectList(
                new LambdaQueryWrapper<CustomerOrder>()
                        .eq(CustomerOrder::getCustomerId, customerId)
                        .orderByDesc(CustomerOrder::getContractDate))
                .stream().map(CustomerOrderConvert::entity2vo).collect(Collectors.toList());
    }

    @Override
    public CustomerOrderVO createOrder(Long customerId, CustomerOrderCreateDTO createDTO) {
        CustomerOrder order = CustomerOrderConvert.dto2entity(createDTO);
        order.setCustomerId(customerId);
        if (order.getOrderStatus() == null) order.setOrderStatus("洽谈中");
        if (order.getOrderNo() == null) {
            order.setOrderNo("ORD-" + System.currentTimeMillis());
        }
        customerOrderMapper.insert(order);
        return CustomerOrderConvert.entity2vo(order);
    }

    @Override
    public void deleteOrder(Long customerId, Long orderId) {
        customerOrderMapper.deleteById(orderId);
    }

    // ==================== 产品与方案 CRUD ====================

    @Override
    public List<CustomerProductVO> listProducts(Long customerId) {
        return customerProductMapper.selectList(
                new LambdaQueryWrapper<CustomerProduct>()
                        .eq(CustomerProduct::getCustomerId, customerId)
                        .orderByDesc(CustomerProduct::getStartDate))
                .stream().map(CustomerProductConvert::entity2vo).collect(Collectors.toList());
    }

    @Override
    public CustomerProductVO createProduct(Long customerId, CustomerProductCreateDTO createDTO) {
        CustomerProduct product = CustomerProductConvert.dto2entity(createDTO);
        product.setCustomerId(customerId);
        if (product.getUsageStatus() == null) product.setUsageStatus("使用中");
        customerProductMapper.insert(product);
        return CustomerProductConvert.entity2vo(product);
    }

    @Override
    public void deleteProduct(Long customerId, Long productId) {
        customerProductMapper.deleteById(productId);
    }

    // ==================== 私有方法 ====================

    private CustomerBranchVO buildBranchTree(CustomerBranch branch, Long customerId) {
        CustomerBranchVO vo = CustomerBranchConvert.entity2vo(branch);
        List<CustomerBranch> children = customerBranchMapper.selectList(
                new LambdaQueryWrapper<CustomerBranch>()
                        .eq(CustomerBranch::getCustomerId, customerId)
                        .eq(CustomerBranch::getParentBranchId, branch.getId()));
        vo.setChildren(children.stream()
                .map(c -> buildBranchTree(c, customerId))
                .collect(Collectors.toList()));
        return vo;
    }

    private String getStatusDesc(Byte status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "未看管";
            case 1 -> "已看管";
            case 2 -> "已剔除";
            default -> "未知";
        };
    }
}
