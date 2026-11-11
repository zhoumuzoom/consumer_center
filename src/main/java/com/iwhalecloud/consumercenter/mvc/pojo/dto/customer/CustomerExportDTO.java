package com.iwhalecloud.consumercenter.mvc.pojo.dto.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户导出DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "客户导出请求")
public class CustomerExportDTO extends CustomerQueryDTO {

    @Schema(description = "导出语言(zh-CN/en-US)", defaultValue = "zh-CN")
    private String lang = "zh-CN";
}
