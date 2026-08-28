package com.iwhalecloud.consumercenter.mvc.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 客户历史订单/项目表
 */
@Data
@TableName("customer_order")
public class CustomerOrder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目类型(信息化/基建/咨询/运维/其他)
     */
    private String projectType;

    /**
     * 订单金额(万元)
     */
    private BigDecimal orderAmount;

    /**
     * 签约日期
     */
    private LocalDateTime contractDate;

    /**
     * 交付日期
     */
    private LocalDateTime deliveryDate;

    /**
     * 订单状态(洽谈中/已签约/执行中/已交付/已验收/已终止)
     */
    private String orderStatus;

    /**
     * 逻辑删除(0:正常,1:删除)
     */
    @TableLogic
    private Byte deleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 项目描述
     */
    private String description;
}