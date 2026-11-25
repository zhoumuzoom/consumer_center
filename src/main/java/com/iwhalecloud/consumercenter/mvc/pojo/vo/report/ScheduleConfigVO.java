package com.iwhalecloud.consumercenter.mvc.pojo.vo.report;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 定时上报配置VO
 */
@Data
public class ScheduleConfigVO {

    private Long id;

    private Boolean enabled;

    private String cronExpression;

    private String cronDescription;

    private Boolean includeAbnormal;

    private LocalDateTime lastExecuteTime;
}
