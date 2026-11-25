package com.iwhalecloud.consumercenter.mvc.pojo.vo.decision;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 决策链VO
 */
@Data
public class DecisionChainVO {

    private Long id;

    private Long customerId;

    private String customerName;

    private Long decisionMakerId;

    private String decisionMakerName;

    private String decisionMakerPosition;

    private String projectName;

    private String decisionStage;

    private String influenceLevel;

    private Integer sortOrder;

    private String remark;

    private LocalDateTime createTime;
}
