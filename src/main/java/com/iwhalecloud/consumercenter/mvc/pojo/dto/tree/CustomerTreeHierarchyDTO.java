package com.iwhalecloud.consumercenter.mvc.pojo.dto.tree;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 调整客户树层级关系DTO
 */
@Data
public class CustomerTreeHierarchyDTO {

    @NotNull(message = "新父节点ID不能为空")
    private Long newParentId;

    private Integer sortOrder;
}
