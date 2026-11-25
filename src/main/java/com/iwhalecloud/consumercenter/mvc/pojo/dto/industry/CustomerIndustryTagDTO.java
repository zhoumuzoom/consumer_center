package com.iwhalecloud.consumercenter.mvc.pojo.dto.industry;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 客户行业标签DTO
 */
@Data
public class CustomerIndustryTagDTO {

    @NotNull(message = "行业节点ID不能为空")
    private Long industryNodeId;

    private String tagLevel;

    private String cooperationStatus;
}
