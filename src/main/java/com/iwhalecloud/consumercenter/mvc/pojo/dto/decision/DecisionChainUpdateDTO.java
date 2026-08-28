package com.iwhalecloud.consumercenter.mvc.pojo.dto.decision;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新决策链关系DTO
 */
@Data
@Schema(description = "更新决策链关系请求")
public class DecisionChainUpdateDTO {

    @NotNull(message = "决策链ID不能为空")
    @Schema(description = "决策链ID")
    private Long id;

    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "决策阶段")
    private String decisionStage;

    @Schema(description = "影响力级别")
    private String influenceLevel;

    @Schema(description = "排序序号")
    private Integer sortOrder;

    @Schema(description = "备注")
    private String remark;
}
