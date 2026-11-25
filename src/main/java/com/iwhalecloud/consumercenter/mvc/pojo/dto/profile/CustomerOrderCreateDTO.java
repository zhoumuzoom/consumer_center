package com.iwhalecloud.consumercenter.mvc.pojo.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CustomerOrderCreateDTO {
    @NotBlank(message = "项目名称不能为空")
    private String projectName;

    private String orderNo;

    private String projectType;

    private BigDecimal orderAmount;

    private LocalDateTime contractDate;

    private LocalDateTime deliveryDate;

    private String orderStatus;

    private String description;
}
