package com.iwhalecloud.consumercenter.mvc.pojo.vo.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户详情VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "客户详情信息")
public class CustomerDetailVO extends CustomerVO {

    @Schema(description = "联系邮箱")
    private String contactEmail;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "下属分支数量")
    private Integer branchCount;

    @Schema(description = "关联决策人数量")
    private Integer decisionMakerCount;

    @Schema(description = "交互记录数量")
    private Integer interactionCount;
}
