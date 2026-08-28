package com.iwhalecloud.consumercenter.mvc.pojo.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 上报数据总览VO
 */
@Data
@Schema(description = "上报数据总览")
public class ReportOverviewVO {

    @Schema(description = "总上报次数")
    private Integer totalReports;

    @Schema(description = "成功上报次数")
    private Integer successReports;

    @Schema(description = "失败上报次数")
    private Integer failReports;

    @Schema(description = "最后上报时间")
    private LocalDateTime lastReportTime;

    @Schema(description = "异常数据数量")
    private Integer abnormalDataCount;

    @Schema(description = "总数据量")
    private Integer totalDataCount;
}
