package com.iwhalecloud.consumercenter.mvc.pojo.vo.profile;

import lombok.Data;
import java.util.List;

@Data
public class CustomerBranchVO {
    private Long id;
    private Long customerId;
    private String branchName;
    private String region;
    private String address;
    private String contactPerson;
    private String contactPhone;
    private String businessScope;
    private Long parentBranchId;
    private String branchType;
    private Integer employeeCount;
    private List<CustomerBranchVO> children;
    private Long businessContribution;
    private Double contributionRate;
}
