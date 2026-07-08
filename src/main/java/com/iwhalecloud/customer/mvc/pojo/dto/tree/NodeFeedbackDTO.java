package com.iwhalecloud.customer.mvc.pojo.dto.tree;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 客户树节点信息反馈DTO
 */
@Data
@Schema(description = "客户树节点信息反馈请求")
public class NodeFeedbackDTO {

    @Schema(description = "反馈内容")
    private String feedbackContent;

    @Schema(description = "新增信息")
    private String newInfo;

    @Schema(description = "业务变动数据")
    private String businessChange;
}
