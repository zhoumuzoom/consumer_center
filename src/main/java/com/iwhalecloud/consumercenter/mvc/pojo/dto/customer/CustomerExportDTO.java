package com.iwhalecloud.consumercenter.mvc.pojo.dto.customer;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户导出DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerExportDTO extends CustomerQueryDTO {

    private String lang = "zh-CN";
}
