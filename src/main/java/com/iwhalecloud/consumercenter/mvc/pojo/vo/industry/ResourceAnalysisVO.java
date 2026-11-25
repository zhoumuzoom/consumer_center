package com.iwhalecloud.consumercenter.mvc.pojo.vo.industry;

import lombok.Data;

/**
 * 行业资源穿透分析VO
 */
@Data
public class ResourceAnalysisVO {

    private Long nodeId;

    private String nodeName;

    private Integer totalCustomers;

    private Long resourceInvestment;

    private Long businessContribution;

    private Double contributionRate;

    private Double avgCustomerValue;
}
