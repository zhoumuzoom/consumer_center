package com.iwhalecloud.consumercenter.mvc.pojo.dto.tree;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 调整客户树层级关系DTO
 */
@Data
@Schema(description = "调整客户树层级关系请求")
public class CustomerTreeHierarchyDTO {

    @NotNull(message = "新父节点ID不能为空")
    @Schema(description = "新父节点ID")
    private Long newParentId;

    @Schema(description = "排序序号")
    private Integer sortOrder;
}
