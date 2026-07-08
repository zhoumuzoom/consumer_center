package com.iwhalecloud.customer.mvc.pojo.dto.decision;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 建立决策链关系DTO
 */
@Data
@Schema(description = "建立决策链关系请求")
public class DecisionChainCreateDTO {

    @NotNull(message = "客户ID不能为空")
    @Schema(description = "客户ID")
    private Long customerId;

    @NotNull(message = "决策人ID不能为空")
    @Schema(description = "决策人ID")
    private Long decisionMakerId;

    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "决策阶段")
    private String decisionStage;

    @Schema(description = "影响力级别(高/中/低)")
    private String influenceLevel;

    @Schema(description = "排序序号")
    private Integer sortOrder;

    @Schema(description = "备注")
    private String remark;
}
