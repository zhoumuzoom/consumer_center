package com.iwhalecloud.consumercenter.mvc.pojo.vo.tree;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 客户树节点统计VO
 */
@Data
@Schema(description = "客户树节点统计信息")
public class CustomerTreeStatisticsVO {

    @Schema(description = "节点ID")
    private Long nodeId;

    @Schema(description = "节点名称")
    private String nodeName;

    @Schema(description = "客户总数")
    private Integer customerCount;

    @Schema(description = "子节点数量")
    private Integer childNodeCount;

    @Schema(description = "看管率(%)")
    private Double managementRate;
}
