package com.iwhalecloud.consumercenter.mvc.pojo.vo.task;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务VO
 */
@Data
public class TaskVO {

    private Long id;

    private Long treeNodeId;

    private String treeNodeName;

    private Long customerId;

    private String customerName;

    private String taskName;

    private String taskType;

    private String taskContent;

    private String priority;

    private Long assigneeId;

    private String assigneeName;

    private String status;

    private LocalDateTime deadline;

    private LocalDateTime completedTime;

    private LocalDateTime createTime;
}
