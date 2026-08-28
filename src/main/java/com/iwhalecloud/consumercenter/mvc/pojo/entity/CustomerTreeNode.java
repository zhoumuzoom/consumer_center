package com.iwhalecloud.consumercenter.mvc.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 客户树节点表
 */
@Data
@TableName("customer_tree_node")
public class CustomerTreeNode implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 父节点ID(0为根节点)
     */
    private Long parentId;

    /**
     * 层级(1:一级,2:二级...)
     */
    private Integer level;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 节点类型(集团/区域/部门)
     */
    private String nodeType;

    /**
     * 职责描述
     */
    private String responsibility;

    /**
     * 责任人ID
     */
    private Long managerId;

    /**
     * 责任人姓名
     */
    private String managerName;

    /**
     * 下属客户数量
     */
    private Integer customerCount;

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