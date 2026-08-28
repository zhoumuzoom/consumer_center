package com.iwhalecloud.consumercenter.mvc.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
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

    private Long id;
    private Long customerId;
    private String orderNo;
    private String projectName;
    private String projectType;
    private BigDecimal orderAmount;
    private LocalDateTime contractDate;
    private LocalDateTime deliveryDate;
    private String orderStatus;
    private String description;

    @TableLogic
    private Byte deleted;
    @Version
    private Integer version;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
