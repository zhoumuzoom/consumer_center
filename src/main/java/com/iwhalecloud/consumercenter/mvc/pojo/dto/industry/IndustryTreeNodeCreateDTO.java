package com.iwhalecloud.consumercenter.mvc.pojo.dto.industry;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 新增行业树节点DTO
 */
@Data
public class IndustryTreeNodeCreateDTO {

    @NotBlank(message = "行业名称不能为空")
    private String nodeName;

    private Long parentId = 0L;

    @NotBlank(message = "行业编码不能为空")
    private String nodeCode;

    private String description;

    private Integer sortOrder;
}
