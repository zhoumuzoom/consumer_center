package com.iwhalecloud.customer.mvc.pojo.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务VO
 */
@Data
@Schema(description = "任务信息")
public class TaskVO {

    @Schema(description = "任务ID")
    private Long id;

    @Schema(description = "客户树节点ID")
    private Long treeNodeId;

    @Schema(description = "节点名称")
    private String treeNodeName;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "任务名称")
    private String taskName;

    @Schema(description = "任务类型")
    private String taskType;

    @Schema(description = "任务内容")
    private String taskContent;

    @Schema(description = "优先级")
    private String priority;

    @Schema(description = "执行人ID")
    private Long assigneeId;

    @Schema(description = "执行人姓名")
    private String assigneeName;

    @Schema(description = "任务状态")
    private String status;

    @Schema(description = "截止时间")
    private LocalDateTime deadline;

    @Schema(description = "完成时间")
    private LocalDateTime completedTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
