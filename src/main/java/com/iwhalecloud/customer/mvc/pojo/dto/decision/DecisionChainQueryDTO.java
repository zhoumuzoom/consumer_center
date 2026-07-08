package com.iwhalecloud.customer.mvc.pojo.dto.decision;

import com.iwhalecloud.customer.mvc.pojo.dto.common.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 决策链查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "决策链查询请求")
public class DecisionChainQueryDTO extends PageQueryDTO {

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "决策人职位")
    private String position;

    @Schema(description = "决策人姓名")
    private String decisionMakerName;
}
