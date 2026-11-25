package com.iwhalecloud.consumercenter.mvc.pojo.dto.tree;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改客户树节点DTO
 */
@Data
public class CustomerTreeNodeUpdateDTO {

    @NotNull(message = "节点ID不能为空")
    private Long id;

    private String nodeName;

    private String nodeType;

    private String responsibility;

    private String managerName;

    private Integer sortOrder;
}
