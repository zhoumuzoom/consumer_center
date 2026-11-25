package com.iwhalecloud.consumercenter.mvc.pojo.dto.decision;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 新增决策人DTO
 */
@Data
public class DecisionMakerCreateDTO {

    @NotBlank(message = "姓名不能为空")
    private String name;

    private String position;

    private String department;

    private String phone;

    private String email;

    private String role;

    private String company;
}
