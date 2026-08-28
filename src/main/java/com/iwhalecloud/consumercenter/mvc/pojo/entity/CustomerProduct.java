package com.iwhalecloud.consumercenter.mvc.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
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

    /**
     * 主键
     */
    private Long id;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 产品/方案名称
     */
    private String productName;

    /**
     * 类型(产品/解决方案)
     */
    private String productType;

    /**
     * 分类(云计算/大数据/AI/安全/网络/办公/其他)
     */
    private String category;

    /**
     * 使用状态(使用中/已停用/评估中)
     */
    private String usageStatus;

    /**
     * 启用日期
     */
    private LocalDateTime startDate;

    /**
     * 停用日期
     */
    private LocalDateTime endDate;

    /**
     * 年费(万元)
     */
    private BigDecimal annualFee;

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
     * 描述
     */
    private String description;
}