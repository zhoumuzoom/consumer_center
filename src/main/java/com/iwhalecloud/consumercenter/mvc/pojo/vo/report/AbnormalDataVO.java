package com.iwhalecloud.consumercenter.mvc.pojo.vo.report;

import lombok.Data;

/**
 * 异常数据VO
 */
@Data
public class AbnormalDataVO {

    private Long customerId;

    private String customerCode;

    private String customerName;

    private String abnormalType;

    private String abnormalDesc;

    private String abnormalField;

    private String currentValue;

    private String ruleRequirement;
}
