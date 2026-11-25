package com.iwhalecloud.consumercenter.mvc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iwhalecloud.consumercenter.common.error.BusinessException;
import com.iwhalecloud.consumercenter.mvc.mapper.CustomerBranchMapper;
import com.iwhalecloud.consumercenter.mvc.mapper.CustomerMapper;
import com.iwhalecloud.consumercenter.mvc.mapper.CustomerOrderMapper;
import com.iwhalecloud.consumercenter.mvc.mapper.CustomerPolicyMapper;
import com.iwhalecloud.consumercenter.mvc.mapper.CustomerProductMapper;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.ai.AIInsightInputDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.Customer;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerBranch;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerOrder;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerPolicy;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * AI洞察数据加载
 */
@Component
public class AIInsightDataLoader {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CustomerBranchMapper customerBranchMapper;

    @Autowired
    private CustomerOrderMapper customerOrderMapper;

    @Autowired
    private CustomerProductMapper customerProductMapper;

    @Autowired
    private CustomerPolicyMapper customerPolicyMapper;

    public AIInsightInputDTO load(Long customerId) {
        Customer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        AIInsightInputDTO input = new AIInsightInputDTO();
        input.setCustomer(customer);
        input.setBranches(customerBranchMapper.selectList(
                new LambdaQueryWrapper<CustomerBranch>().eq(CustomerBranch::getCustomerId, customerId)));
        input.setOrders(customerOrderMapper.selectList(
                new LambdaQueryWrapper<CustomerOrder>().eq(CustomerOrder::getCustomerId, customerId)));
        input.setProducts(customerProductMapper.selectList(
                new LambdaQueryWrapper<CustomerProduct>().eq(CustomerProduct::getCustomerId, customerId)));
        input.setPolicies(customerPolicyMapper.selectList(
                new LambdaQueryWrapper<CustomerPolicy>()
                        .eq(CustomerPolicy::getCustomerId, customerId)
                        .eq(CustomerPolicy::getStatus, "有效")));
        return input;
    }
}