package com.iwhalecloud.consumercenter.mvc.pojo.dto.customer;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改客户DTO
 */
@Data
public class CustomerUpdateDTO {

    @NotNull(message = "客户ID不能为空")
    private Long id;

    private String customerName;

    private String customerNameEn;

    private String industry;

    private String region;

    private String scale;

    private String contactPerson;

    private String contactPhone;

    private String contactEmail;

    private String address;

    private String remark;
}
