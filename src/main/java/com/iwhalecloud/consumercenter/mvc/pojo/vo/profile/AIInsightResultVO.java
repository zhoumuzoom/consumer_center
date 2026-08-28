package com.iwhalecloud.consumercenter.mvc.pojo.vo.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * AI洞察结果VO
 */
@Data
@Schema(description = "AI洞察结果")
public class AIInsightResultVO {

    @Schema(description = "任务ID")
    private String taskId;

    @Schema(description = "状态(generating/completed/failed)")
    private String status;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "洞察标题")
    private String title;

    @Schema(description = "行业概况")
    private String industryOverview;

    @Schema(description = "客户分析")
    private String customerAnalysis;

    @Schema(description = "竞争格局")
    private String competitiveLandscape;

    @Schema(description = "机会与建议")
    private String opportunitiesAndSuggestions;

    @Schema(description = "风险提示")
    private String riskAlerts;

    @Schema(description = "生成时间")
    private String generatedTime;
}
