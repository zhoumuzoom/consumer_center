package com.iwhalecloud.customer.mvc.pojo.dto.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 新增客户DTO
 */
@Data
@Schema(description = "新增客户请求")
public class CustomerCreateDTO {

    @NotBlank(message = "客户名称不能为空")
    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "客户英文名称")
    private String customerNameEn;

    @Schema(description = "所属行业")
    private String industry;

    @Schema(description = "所属区域")
    private String region;

    @Schema(description = "企业规模")
    private String scale;

    @Schema(description = "联系人")
    private String contactPerson;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "联系邮箱")
    private String contactEmail;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "备注")
    private String remark;
}
