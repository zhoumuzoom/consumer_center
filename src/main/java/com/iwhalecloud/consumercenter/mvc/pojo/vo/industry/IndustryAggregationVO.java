package com.iwhalecloud.consumercenter.mvc.pojo.vo.industry;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 行业聚合数据VO
 */
@Data
@Schema(description = "行业聚合数据")
public class IndustryAggregationVO {

    @Schema(description = "节点ID")
    private Long nodeId;

    @Schema(description = "节点名称")
    private String nodeName;

    @Schema(description = "客户数量")
    private Integer customerCount;

    @Schema(description = "看管率(%)")
    private Double managementRate;

    @Schema(description = "集团客户数量")
    private Integer groupCount;

    @Schema(description = "战略要客数量")
    private Integer strategicCount;

    @Schema(description = "重点客户数量")
    private Integer keyCount;
}
