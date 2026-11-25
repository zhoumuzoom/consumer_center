package com.iwhalecloud.consumercenter.mvc.pojo.vo.profile;

import lombok.Data;

/**
 * AI洞察结果VO
 */
@Data
public class AIInsightResultVO {

    private String taskId;

    private String status;

    private String customerName;

    private String title;

    private String industryOverview;

    private String customerAnalysis;

    private String competitiveLandscape;

    private String opportunitiesAndSuggestions;

    private String riskAlerts;

    private String generatedTime;

    private String errorMessage;
}
