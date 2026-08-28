package com.iwhalecloud.consumercenter.mvc.pojo.dto.tree;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改客户树节点DTO
 */
@Data
@Schema(description = "修改客户树节点请求")
public class CustomerTreeNodeUpdateDTO {

    @NotNull(message = "节点ID不能为空")
    @Schema(description = "节点ID")
    private Long id;

    @Schema(description = "节点名称")
    private String nodeName;

    @Schema(description = "节点类型")
    private String nodeType;

    @Schema(description = "职责描述")
    private String responsibility;

    @Schema(description = "责任人姓名")
    private String managerName;

    @Schema(description = "排序序号")
    private Integer sortOrder;
}
