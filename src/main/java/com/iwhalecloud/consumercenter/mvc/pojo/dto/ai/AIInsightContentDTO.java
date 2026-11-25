package com.iwhalecloud.consumercenter.mvc.pojo.dto.ai;

import lombok.Data;

/**
 * AI洞察五段内容
 */
@Data
public class AIInsightContentDTO {

    private String industryOverview;

    private String customerAnalysis;

    private String competitiveLandscape;

    private String opportunitiesAndSuggestions;

    private String riskAlerts;
}