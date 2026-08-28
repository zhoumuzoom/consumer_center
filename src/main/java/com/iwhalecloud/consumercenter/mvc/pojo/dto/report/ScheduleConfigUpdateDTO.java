package com.iwhalecloud.consumercenter.mvc.pojo.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新定时上报配置DTO
 */
@Data
@Schema(description = "更新定时上报配置请求")
public class ScheduleConfigUpdateDTO {

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "Cron表达式")
    private String cronExpression;

    @Schema(description = "是否包含异常数据")
    private Boolean includeAbnormal;
}
