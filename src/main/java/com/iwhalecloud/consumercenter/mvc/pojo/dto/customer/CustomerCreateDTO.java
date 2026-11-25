package com.iwhalecloud.consumercenter.mvc.pojo.dto.customer;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 新增客户DTO
 */
@Data
public class CustomerCreateDTO {

    @NotBlank(message = "客户名称不能为空")
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
