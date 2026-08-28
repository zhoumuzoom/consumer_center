package com.iwhalecloud.consumercenter.mvc.pojo.dto.tree;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 新增客户树节点DTO
 */
@Data
@Schema(description = "新增客户树节点请求")
public class CustomerTreeNodeCreateDTO {

    @NotBlank(message = "节点名称不能为空")
    @Schema(description = "节点名称")
    private String nodeName;

    @Schema(description = "父节点ID，0为根节点")
    private Long parentId = 0L;

    @Schema(description = "节点类型(集团/区域/部门)")
    private String nodeType;

    @Schema(description = "职责描述")
    private String responsibility;

    @Schema(description = "责任人姓名")
    private String managerName;
}
