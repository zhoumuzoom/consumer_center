package com.iwhalecloud.consumercenter.mvc.pojo.vo.industry;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 行业资源穿透分析VO
 */
@Data
@Schema(description = "行业资源穿透分析结果")
public class ResourceAnalysisVO {

    @Schema(description = "节点ID")
    private Long nodeId;

    @Schema(description = "节点名称")
    private String nodeName;

    @Schema(description = "客户总数")
    private Integer totalCustomers;

    @Schema(description = "资源投入(万元)")
    private Long resourceInvestment;

    @Schema(description = "业务贡献(万元)")
    private Long businessContribution;

    @Schema(description = "贡献率(%)")
    private Double contributionRate;

    @Schema(description = "平均客单价(万元)")
    private Double avgCustomerValue;
}
