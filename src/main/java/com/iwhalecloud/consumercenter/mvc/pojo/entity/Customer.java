package com.iwhalecloud.consumercenter.mvc.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 客户表
 */
@Data
@TableName("customer")
public class Customer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 客户编号
     */
    private String customerCode;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 客户英文名称
     */
    private String customerNameEn;

    /**
     * 所属行业
     */
    private String industry;

    /**
     * 所属区域
     */
    private String region;

    /**
     * 企业规模(大型/中型/小型)
     */
    private String scale;

    /**
     * 看管状态(0:未看管,1:已看管,2:已剔除)
     */
    private Byte status;

    /**
     * 客户经理ID
     */
    private Long managerId;

    /**
     * 客户经理姓名
     */
    private String managerName;

    /**
     * 入库时间
     */
    private LocalDateTime joinTime;

    /**
     * 数据来源(139系统/出海系统/手动录入)
     */
    private String source;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系邮箱
     */
    private String contactEmail;

    /**
     * 地址
     */
    private String address;

    /**
     * 备注
     */
    private String remark;

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
}