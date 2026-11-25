package com.iwhalecloud.consumercenter.mvc.pojo.dto.report;

import lombok.Data;

/**
 * 更新定时上报配置DTO
 */
@Data
public class ScheduleConfigUpdateDTO {

    private Boolean enabled;

    private String cronExpression;

    private Boolean includeAbnormal;
}
