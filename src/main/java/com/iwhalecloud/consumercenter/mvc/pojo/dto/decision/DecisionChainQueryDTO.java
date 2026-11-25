package com.iwhalecloud.consumercenter.mvc.pojo.dto.decision;

import com.iwhalecloud.consumercenter.common.param.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 决策链查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DecisionChainQueryDTO extends PageQuery {

    private String customerName;

    private String projectName;

    private String position;

    private String decisionMakerName;
}
