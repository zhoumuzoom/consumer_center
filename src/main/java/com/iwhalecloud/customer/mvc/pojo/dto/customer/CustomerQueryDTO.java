package com.iwhalecloud.customer.mvc.pojo.dto.customer;

import com.iwhalecloud.customer.mvc.pojo.dto.common.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "客户查询请求")
public class CustomerQueryDTO extends PageQueryDTO {

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "所属行业")
    private String industry;

    @Schema(description = "所属区域")
    private String region;

    @Schema(description = "企业规模")
    private String scale;

    @Schema(description = "看管状态(0:未看管,1:已看管,2:已剔除)")
    private Integer status;

    @Schema(description = "数据来源")
    private String source;
}
