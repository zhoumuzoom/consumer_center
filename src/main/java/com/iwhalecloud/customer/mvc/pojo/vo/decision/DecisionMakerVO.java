package com.iwhalecloud.customer.mvc.pojo.vo.decision;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 决策人VO
 */
@Data
@Schema(description = "决策人信息")
public class DecisionMakerVO {

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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
