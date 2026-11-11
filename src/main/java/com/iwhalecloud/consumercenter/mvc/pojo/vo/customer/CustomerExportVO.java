package com.iwhalecloud.consumercenter.mvc.pojo.vo.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 客户导出结果
 */
@Data
@Schema(description = "客户导出结果")
public class CustomerExportVO {

    @Schema(description = "导出文件内容")
    private byte[] content;

    @Schema(description = "导出记录数")
    private Integer total;

    @Schema(description = "导出文件名")
    private String filename;
}
