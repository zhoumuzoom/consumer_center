package com.iwhalecloud.consumercenter.mvc.pojo.dto.decision;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 建立决策链关系DTO
 */
@Data
public class DecisionChainCreateDTO {

    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @NotNull(message = "决策人ID不能为空")
    private Long decisionMakerId;

    private String projectName;

    private String decisionStage;

    private String influenceLevel;

    private Integer sortOrder;

    private String remark;
}
