package com.iwhalecloud.customer.mvc.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 操作日志表
 */
@Data
@TableName("operation_log")
public class OperationLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 实体ID
     */
    private Long entityId;

    /**
     * 实体类型(customer/tree_node/industry_node等)
     */
    private String entityType;

    /**
     * 操作类型(CREATE/UPDATE/DELETE/STATUS_CHANGE)
     */
    private String operationType;

    /**
     * 操作描述
     */
    private String operationDesc;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 操作时间
     */
    private LocalDateTime operationTime;

    /**
     * 操作前数据(JSON)
     */
    private String beforeData;

    /**
     * 操作后数据(JSON)
     */
    private String afterData;
}