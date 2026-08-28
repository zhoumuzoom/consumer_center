package com.iwhalecloud.consumercenter.mvc.pojo.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 定时上报配置VO
 */
@Data
@Schema(description = "定时上报配置")
public class ScheduleConfigVO {

    @Schema(description = "配置ID")
    private Long id;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "Cron表达式")
    private String cronExpression;

    @Schema(description = "Cron描述")
    private String cronDescription;

    @Schema(description = "是否包含异常数据")
    private Boolean includeAbnormal;

    @Schema(description = "上次执行时间")
    private LocalDateTime lastExecuteTime;
}
