package com.iwhalecloud.customer.mvc.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 客户政策动态表
 */
@Data
@TableName("customer_policy")
public class CustomerPolicy implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long customerId;
    private String policyTitle;
    private String policyType;
    private String policyLevel;
    private String issuingAuthority;
    private LocalDateTime publishDate;
    private LocalDateTime effectiveDate;
    private LocalDateTime expireDate;
    private String policySummary;
    private String impactAnalysis;
    private String sourceUrl;
    private String status;

    @TableLogic
    private Byte deleted;
    @Version
    private Integer version;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
