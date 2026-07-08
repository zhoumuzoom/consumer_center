package com.iwhalecloud.customer.mvc.pojo.vo.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分支业务分析VO
 */
@Data
@Schema(description = "分支业务分析结果")
public class BranchAnalysisVO {

    @Schema(description = "分支ID")
    private Long branchId;

    @Schema(description = "分支名称")
    private String branchName;

    @Schema(description = "所在区域")
    private String region;

    @Schema(description = "员工人数")
    private Integer employeeCount;

    @Schema(description = "业务贡献(万元)")
    private Long businessContribution;

    @Schema(description = "资源投入(万元)")
    private Long resourceInvestment;

    @Schema(description = "贡献占比(%)")
    private Double contributionRate;

    @Schema(description = "ROI(%)")
    private Double roi;
}
