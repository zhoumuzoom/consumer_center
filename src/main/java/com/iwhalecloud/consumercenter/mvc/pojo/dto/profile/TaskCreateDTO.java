package com.iwhalecloud.consumercenter.mvc.pojo.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 创建任务DTO
 */
@Data
public class TaskCreateDTO {

    @NotBlank(message = "任务名称不能为空")
    private String taskName;

    private String taskType;

    private String taskContent;

    private String priority = "中";

    private Long treeNodeId;

    private Long customerId;

    private String assigneeName;

    private LocalDateTime deadline;
}
