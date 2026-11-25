package com.iwhalecloud.consumercenter.mvc.pojo.dto.ai;

import com.iwhalecloud.consumercenter.mvc.pojo.entity.Customer;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerBranch;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerOrder;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerPolicy;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerProduct;
import lombok.Data;

import java.util.List;

/**
 * AI洞察输入数据
 */
@Data
public class AIInsightInputDTO {

    private Customer customer;

    private List<CustomerBranch> branches;

    private List<CustomerOrder> orders;

    private List<CustomerProduct> products;

    private List<CustomerPolicy> policies;
}