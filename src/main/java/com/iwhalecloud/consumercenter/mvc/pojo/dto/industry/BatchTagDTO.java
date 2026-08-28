package com.iwhalecloud.consumercenter.mvc.pojo.dto.industry;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 批量设置客户标签DTO
 */
@Data
@Schema(description = "批量设置客户标签请求")
public class BatchTagDTO {

    @NotEmpty(message = "客户ID列表不能为空")
    @Schema(description = "客户ID列表")
    private List<Long> customerIds;

    @NotNull(message = "行业节点ID不能为空")
    @Schema(description = "行业节点ID")
    private Long industryNodeId;

    @Schema(description = "标签级别")
    private String tagLevel;

    @Schema(description = "合作状态")
    private String cooperationStatus;
}
