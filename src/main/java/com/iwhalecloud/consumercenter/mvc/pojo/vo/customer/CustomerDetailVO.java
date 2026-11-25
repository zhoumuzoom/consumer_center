package com.iwhalecloud.consumercenter.mvc.pojo.vo.customer;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户详情VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerDetailVO extends CustomerVO {

    private String contactEmail;

    private String address;

    private String remark;

    private Integer branchCount;

    private Integer decisionMakerCount;

    private Integer interactionCount;
}
