package com.iwhalecloud.consumercenter.mvc.pojo.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 创建任务DTO
 */
@Data
@Schema(description = "创建任务请求")
public class TaskCreateDTO {

    @NotBlank(message = "任务名称不能为空")
    @Schema(description = "任务名称")
    private String taskName;

    @Schema(description = "任务类型(营销/运营)")
    private String taskType;

    @Schema(description = "任务内容")
    private String taskContent;

    @Schema(description = "优先级(高/中/低)")
    private String priority = "中";

    @Schema(description = "客户树节点ID")
    private Long treeNodeId;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "执行人姓名")
    private String assigneeName;

    @Schema(description = "截止时间")
    private LocalDateTime deadline;
}
