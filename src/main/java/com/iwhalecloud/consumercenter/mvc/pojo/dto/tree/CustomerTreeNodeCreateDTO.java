package com.iwhalecloud.consumercenter.mvc.pojo.dto.tree;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 新增客户树节点DTO
 */
@Data
public class CustomerTreeNodeCreateDTO {

    @NotBlank(message = "节点名称不能为空")
    private String nodeName;

    private Long parentId = 0L;

    private String nodeType;

    private String responsibility;

    private String managerName;
}
