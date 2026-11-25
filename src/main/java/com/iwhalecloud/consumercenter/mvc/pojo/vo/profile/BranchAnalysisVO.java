package com.iwhalecloud.consumercenter.mvc.pojo.vo.profile;

import lombok.Data;

/**
 * 分支业务分析VO
 */
@Data
public class BranchAnalysisVO {

    private Long branchId;

    private String branchName;

    private String region;

    private Integer employeeCount;

    private Long businessContribution;

    private Long resourceInvestment;

    private Double contributionRate;

    private Double roi;
}
