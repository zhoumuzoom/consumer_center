package com.iwhalecloud.consumercenter.mvc.pojo.vo.tree;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 客户树节点VO
 */
@Data
@Schema(description = "客户树节点信息")
public class CustomerTreeNodeVO {

    @Schema(description = "节点ID")
    private Long id;

    @Schema(description = "节点名称")
    private String nodeName;

    @Schema(description = "父节点ID")
    private Long parentId;

    @Schema(description = "层级")
    private Integer level;

    @Schema(description = "排序序号")
    private Integer sortOrder;

    @Schema(description = "节点类型")
    private String nodeType;

    @Schema(description = "职责描述")
    private String responsibility;

    @Schema(description = "责任人姓名")
    private String managerName;

    @Schema(description = "下属客户数量")
    private Integer customerCount;

    @Schema(description = "子节点列表")
    private List<CustomerTreeNodeVO> children;
}
