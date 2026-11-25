package com.iwhalecloud.consumercenter.mvc.pojo.dto.customer;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 客户状态变更DTO
 */
@Data
public class CustomerStatusUpdateDTO {

    @NotNull(message = "状态不能为空")
    private Integer status;

    private String reason;
}
