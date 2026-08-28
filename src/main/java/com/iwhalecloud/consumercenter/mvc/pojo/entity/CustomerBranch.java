package com.iwhalecloud.consumercenter.mvc.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
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

    private Long id;
    private Long customerId;
    private String branchName;
    private String region;
    private String address;
    private String contactPerson;
    private String contactPhone;
    private String businessScope;
    private Long parentBranchId;
    /** 分支类型(总部/海外分支/本地实体) */
    private String branchType;
    private Integer employeeCount;

    @TableLogic
    private Byte deleted;
    @Version
    private Integer version;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
