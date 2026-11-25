package com.iwhalecloud.consumercenter.mvc.pojo.dto.decision;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新决策链关系DTO
 */
@Data
public class DecisionChainUpdateDTO {

    @NotNull(message = "决策链ID不能为空")
    private Long id;

    private String projectName;

    private String decisionStage;

    private String influenceLevel;

    private Integer sortOrder;

    private String remark;
}
