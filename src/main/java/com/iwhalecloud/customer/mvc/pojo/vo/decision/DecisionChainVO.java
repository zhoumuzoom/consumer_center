package com.iwhalecloud.customer.mvc.pojo.vo.decision;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 决策链VO
 */
@Data
@Schema(description = "决策链信息")
public class DecisionChainVO {

    @Schema(description = "决策链ID")
    private Long id;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "决策人ID")
    private Long decisionMakerId;

    @Schema(description = "决策人姓名")
    private String decisionMakerName;

    @Schema(description = "决策人职位")
    private String decisionMakerPosition;

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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
