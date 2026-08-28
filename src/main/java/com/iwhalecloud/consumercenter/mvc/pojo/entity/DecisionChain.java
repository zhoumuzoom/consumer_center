package com.iwhalecloud.consumercenter.mvc.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 决策链关系表
 */
@Data
@TableName("decision_chain")
public class DecisionChain implements Serializable {

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
     * 决策人ID
     */
    private Long decisionMakerId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 决策阶段(需求发起/方案审批/项目实施)
     */
    private String decisionStage;

    /**
     * 影响力级别(高/中/低)
     */
    private String influenceLevel;

    /**
     * 排序序号
     */
    private Integer sortOrder;

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