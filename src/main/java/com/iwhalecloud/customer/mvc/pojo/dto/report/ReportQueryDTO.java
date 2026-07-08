package com.iwhalecloud.customer.mvc.pojo.dto.report;

import com.iwhalecloud.customer.mvc.pojo.dto.common.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 上报记录查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "上报记录查询请求")
public class ReportQueryDTO extends PageQueryDTO {

    @Schema(description = "上报状态")
    private String status;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}
