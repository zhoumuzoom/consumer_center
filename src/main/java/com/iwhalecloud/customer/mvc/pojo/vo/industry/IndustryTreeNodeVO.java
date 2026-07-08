package com.iwhalecloud.customer.mvc.pojo.vo.industry;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 行业树节点VO
 */
@Data
@Schema(description = "行业树节点信息")
public class IndustryTreeNodeVO {

    @Schema(description = "节点ID")
    private Long id;

    @Schema(description = "行业名称")
    private String nodeName;

    @Schema(description = "父节点ID")
    private Long parentId;

    @Schema(description = "层级")
    private Integer level;

    @Schema(description = "排序序号")
    private Integer sortOrder;

    @Schema(description = "行业编码")
    private String nodeCode;

    @Schema(description = "行业描述")
    private String description;

    @Schema(description = "客户数量")
    private Integer customerCount;

    @Schema(description = "子节点列表")
    private List<IndustryTreeNodeVO> children;
}
