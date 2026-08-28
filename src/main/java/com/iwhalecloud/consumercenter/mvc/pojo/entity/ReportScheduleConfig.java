package com.iwhalecloud.consumercenter.mvc.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 定时上报配置表
 */
@Data
@TableName("report_schedule_config")
public class ReportScheduleConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 是否启用(0:禁用,1:启用)
     */
    private Byte enabled;

    /**
     * Cron表达式
     */
    private String cronExpression;

    /**
     * 是否包含异常数据(0:不包含,1:包含)
     */
    private Byte includeAbnormal;

    /**
     * 上次执行时间
     */
    private LocalDateTime lastExecuteTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}