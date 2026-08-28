package com.iwhalecloud.consumercenter.mvc.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 客户交互记录表
 */
@Data
@TableName("customer_interaction")
public class CustomerInteraction implements Serializable {

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
     * 交互类型(沟通/方案提交/客户反馈/拜访/会议)
     */
    private String interactionType;

    /**
     * 参与人
     */
    private String participant;

    /**
     * 交互时间
     */
    private LocalDateTime interactionTime;

    /**
     * 附件URL
     */
    private String attachmentUrl;

    /**
     * 交互结果
     */
    private String result;

    /**
     * 逻辑删除(0:正常,1:删除)
     */
    @TableLogic
    private Byte deleted;

    /**
     * 乐观锁版本号
     */
    @Version
    private Integer version;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 交互内容
     */
    private String interactionContent;
}