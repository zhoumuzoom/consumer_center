package com.iwhalecloud.consumercenter.mvc.pojo.vo.decision;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 决策人VO
 */
@Data
public class DecisionMakerVO {

    private Long id;

    private String name;

    private String position;

    private String department;

    private String phone;

    private String email;

    private String role;

    private String company;

    private LocalDateTime createTime;
}
