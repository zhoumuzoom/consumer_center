package com.iwhalecloud.consumercenter.mvc.pojo.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 手动上报DTO
 */
@Data
@Schema(description = "手动上报请求")
public class ManualReportDTO {

    @Schema(description = "是否包含异常数据")
    private Boolean includeAbnormal = false;
}
