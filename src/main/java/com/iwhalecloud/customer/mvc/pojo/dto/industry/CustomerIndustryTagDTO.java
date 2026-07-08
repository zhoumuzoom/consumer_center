package com.iwhalecloud.customer.mvc.pojo.dto.industry;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 客户行业标签DTO
 */
@Data
@Schema(description = "客户行业标签请求")
public class CustomerIndustryTagDTO {

    @NotNull(message = "行业节点ID不能为空")
    @Schema(description = "行业节点ID")
    private Long industryNodeId;

    @Schema(description = "标签级别(战略要客/重点客户)")
    private String tagLevel;

    @Schema(description = "合作状态(合作中/洽谈中/已终止)")
    private String cooperationStatus;
}
