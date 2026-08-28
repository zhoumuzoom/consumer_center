package com.iwhalecloud.consumercenter.mvc.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 客户分支表
 */
@Data
@TableName("customer_branch")
public class CustomerBranch implements Serializable {

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
     * 分支名称
     */
    private String branchName;

    /**
     * 所在区域
     */
    private String region;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 业务范围
     */
    private String businessScope;

    /**
     * 父分支ID(0为总部直属)
     */
    private Long parentBranchId;

    /**
     * 分支类型(总部/海外分支/本地实体)
     */
    private String branchType;

    /**
     * 员工人数
     */
    private Integer employeeCount;

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