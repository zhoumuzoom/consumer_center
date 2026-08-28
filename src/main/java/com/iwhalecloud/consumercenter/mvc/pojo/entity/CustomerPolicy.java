package com.iwhalecloud.consumercenter.mvc.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
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

    /**
     * 主键
     */
    private Long id;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 政策标题
     */
    private String policyTitle;

    /**
     * 政策类型(产业/财税/贸易/金融/其他)
     */
    private String policyType;

    /**
     * 政策级别(国家/省级/市级/行业)
     */
    private String policyLevel;

    /**
     * 发布机构
     */
    private String issuingAuthority;

    /**
     * 发布日期
     */
    private LocalDateTime publishDate;

    /**
     * 生效日期
     */
    private LocalDateTime effectiveDate;

    /**
     * 失效日期
     */
    private LocalDateTime expireDate;

    /**
     * 政策来源链接
     */
    private String sourceUrl;

    /**
     * 状态(有效/已过期/已废止)
     */
    private String status;

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
     * 政策摘要
     */
    private String policySummary;

    /**
     * 对客户的影响分析
     */
    private String impactAnalysis;
}