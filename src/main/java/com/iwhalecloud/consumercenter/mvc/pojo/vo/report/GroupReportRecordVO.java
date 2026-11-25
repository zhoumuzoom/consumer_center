package com.iwhalecloud.consumercenter.mvc.pojo.vo.report;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 集团上报记录VO
 */
@Data
public class GroupReportRecordVO {

    private Long id;

    private String reportBatch;

    private String reportType;

    private Integer totalCount;

    private Integer successCount;

    private Integer failCount;

    private String status;

    private String errorMsg;

    private LocalDateTime reportTime;

    private LocalDateTime createTime;
}
