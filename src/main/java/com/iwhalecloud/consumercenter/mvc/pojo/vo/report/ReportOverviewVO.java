package com.iwhalecloud.consumercenter.mvc.pojo.vo.report;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 上报数据总览VO
 */
@Data
public class ReportOverviewVO {

    private Integer totalReports;

    private Integer successReports;

    private Integer failReports;

    private LocalDateTime lastReportTime;

    private Integer abnormalDataCount;

    private Integer totalDataCount;
}
