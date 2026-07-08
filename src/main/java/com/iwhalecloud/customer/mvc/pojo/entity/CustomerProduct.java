package com.iwhalecloud.customer.mvc.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 客户产品与方案表
 */
@Data
@TableName("customer_product")
public class CustomerProduct implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long customerId;
    private String productName;
    private String productType;
    private String category;
    private String usageStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal annualFee;
    private String description;

    @TableLogic
    private Byte deleted;
    @Version
    private Integer version;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
