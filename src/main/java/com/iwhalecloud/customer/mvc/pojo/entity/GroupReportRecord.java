package com.iwhalecloud.customer.mvc.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 集团上报记录表
 */
@Data
@TableName("group_report_record")
public class GroupReportRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 上报批次号
     */
    private String reportBatch;

    /**
     * 上报类型(手动/定时)
     */
    private String reportType;

    /**
     * 总数据量
     */
    private Integer totalCount;

    /**
     * 成功数量
     */
    private Integer successCount;

    /**
     * 失败数量
     */
    private Integer failCount;

    /**
     * 上报状态(处理中/成功/部分失败/失败)
     */
    private String status;

    /**
     * 上报时间
     */
    private LocalDateTime reportTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 上报数据JSON
     */
    private String reportData;

    /**
     * 错误信息
     */
    private String errorMsg;
}