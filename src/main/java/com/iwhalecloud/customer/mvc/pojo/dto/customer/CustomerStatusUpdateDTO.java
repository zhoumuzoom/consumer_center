package com.iwhalecloud.customer.mvc.pojo.dto.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 客户状态变更DTO
 */
@Data
@Schema(description = "客户状态变更请求")
public class CustomerStatusUpdateDTO {

    @NotNull(message = "状态不能为空")
    @Schema(description = "看管状态(0:未看管,1:已看管,2:已剔除)")
    private Integer status;

    @Schema(description = "变更原因")
    private String reason;
}
