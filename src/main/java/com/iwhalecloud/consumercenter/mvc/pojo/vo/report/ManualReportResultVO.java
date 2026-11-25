package com.iwhalecloud.consumercenter.mvc.pojo.vo.report;

import lombok.Data;

/**
 * 手动上报结果VO
 */
@Data
public class ManualReportResultVO {

    private String reportBatch;

    private Integer totalCount;

    private Integer successCount;

    private Integer failCount;
}
