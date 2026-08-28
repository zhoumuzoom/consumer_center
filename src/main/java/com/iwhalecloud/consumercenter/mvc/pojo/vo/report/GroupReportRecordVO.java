package com.iwhalecloud.consumercenter.mvc.pojo.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 集团上报记录VO
 */
@Data
@Schema(description = "集团上报记录")
public class GroupReportRecordVO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "上报批次号")
    private String reportBatch;

    @Schema(description = "上报类型")
    private String reportType;

    @Schema(description = "总数据量")
    private Integer totalCount;

    @Schema(description = "成功数量")
    private Integer successCount;

    @Schema(description = "失败数量")
    private Integer failCount;

    @Schema(description = "上报状态")
    private String status;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "上报时间")
    private LocalDateTime reportTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
