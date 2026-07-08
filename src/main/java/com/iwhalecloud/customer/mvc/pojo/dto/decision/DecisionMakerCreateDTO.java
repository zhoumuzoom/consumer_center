package com.iwhalecloud.customer.mvc.pojo.dto.decision;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 新增决策人DTO
 */
@Data
@Schema(description = "新增决策人请求")
public class DecisionMakerCreateDTO {

    @NotBlank(message = "姓名不能为空")
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

    @Schema(description = "角色(需求发起/方案审批/项目实施)")
    private String role;

    @Schema(description = "所属公司")
    private String company;
}
