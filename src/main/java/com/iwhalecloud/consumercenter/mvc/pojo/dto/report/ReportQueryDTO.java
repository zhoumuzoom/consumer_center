package com.iwhalecloud.consumercenter.mvc.pojo.dto.report;

import com.iwhalecloud.consumercenter.common.param.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 上报记录查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReportQueryDTO extends PageQuery {

    private String status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
