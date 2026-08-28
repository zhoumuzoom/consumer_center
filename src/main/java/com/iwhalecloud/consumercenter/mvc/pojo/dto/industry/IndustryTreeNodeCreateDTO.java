package com.iwhalecloud.consumercenter.mvc.pojo.dto.industry;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 新增行业树节点DTO
 */
@Data
@Schema(description = "新增行业树节点请求")
public class IndustryTreeNodeCreateDTO {

    @NotBlank(message = "行业名称不能为空")
    @Schema(description = "行业名称")
    private String nodeName;

    @Schema(description = "父节点ID，0为根节点")
    private Long parentId = 0L;

    @NotBlank(message = "行业编码不能为空")
    @Schema(description = "行业编码")
    private String nodeCode;

    @Schema(description = "行业描述")
    private String description;

    @Schema(description = "排序序号")
    private Integer sortOrder;
}
