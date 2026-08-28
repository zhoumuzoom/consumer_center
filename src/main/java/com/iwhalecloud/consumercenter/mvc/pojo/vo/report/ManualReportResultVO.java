package com.iwhalecloud.consumercenter.mvc.pojo.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 手动上报结果VO
 */
@Data
@Schema(description = "手动上报结果")
public class ManualReportResultVO {

    @Schema(description = "上报批次号")
    private String reportBatch;

    @Schema(description = "总数据量")
    private Integer totalCount;

    @Schema(description = "成功数量")
    private Integer successCount;

    @Schema(description = "失败数量")
    private Integer failCount;
}
