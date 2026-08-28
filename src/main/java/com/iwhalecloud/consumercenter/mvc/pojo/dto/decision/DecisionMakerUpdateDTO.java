package com.iwhalecloud.consumercenter.mvc.pojo.dto.decision;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改决策人DTO
 */
@Data
@Schema(description = "修改决策人请求")
public class DecisionMakerUpdateDTO {

    @NotNull(message = "决策人ID不能为空")
    @Schema(description = "决策人ID")
    private Long id;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "职位")
    private String position;

    @Schema(description = "部门")
    private String department;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "角色")
    private String role;

    @Schema(description = "所属公司")
    private String company;
}
