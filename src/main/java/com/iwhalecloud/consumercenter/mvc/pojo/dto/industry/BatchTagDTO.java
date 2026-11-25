package com.iwhalecloud.consumercenter.mvc.pojo.dto.industry;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 批量设置客户标签DTO
 */
@Data
public class BatchTagDTO {

    @NotEmpty(message = "客户ID列表不能为空")
    private List<Long> customerIds;

    @NotNull(message = "行业节点ID不能为空")
    private Long industryNodeId;

    private String tagLevel;

    private String cooperationStatus;
}
