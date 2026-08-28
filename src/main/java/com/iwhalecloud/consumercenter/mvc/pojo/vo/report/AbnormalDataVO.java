package com.iwhalecloud.consumercenter.mvc.pojo.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 异常数据VO
 */
@Data
@Schema(description = "异常上报数据")
public class AbnormalDataVO {

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "客户编号")
    private String customerCode;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "异常类型")
    private String abnormalType;

    @Schema(description = "异常描述")
    private String abnormalDesc;

    @Schema(description = "异常字段")
    private String abnormalField;

    @Schema(description = "当前值")
    private String currentValue;

    @Schema(description = "规则要求")
    private String ruleRequirement;
}
