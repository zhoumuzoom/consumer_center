package com.iwhalecloud.consumercenter.mvc.pojo.dto.tree;

import lombok.Data;

/**
 * 客户树节点信息反馈DTO
 */
@Data
public class NodeFeedbackDTO {

    private String feedbackContent;

    private String newInfo;

    private String businessChange;
}
