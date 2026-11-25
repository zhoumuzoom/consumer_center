package com.iwhalecloud.consumercenter.mvc.pojo.dto.decision;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改决策人DTO
 */
@Data
public class DecisionMakerUpdateDTO {

    @NotNull(message = "决策人ID不能为空")
    private Long id;

    private String name;

    private String position;

    private String department;

    private String phone;

    private String email;

    private String role;

    private String company;
}
