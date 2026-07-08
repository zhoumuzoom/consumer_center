package com.iwhalecloud.customer.mvc.pojo.vo.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户VO
 */
@Data
@Schema(description = "客户信息")
public class CustomerVO {

    @Schema(description = "客户ID")
    private Long id;

    @Schema(description = "客户编号")
    private String customerCode;

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

    @Schema(description = "看管状态(0:未看管,1:已看管,2:已剔除)")
    private Integer status;

    @Schema(description = "看管状态描述")
    private String statusDesc;

    @Schema(description = "客户经理姓名")
    private String managerName;

    @Schema(description = "入库时间")
    private LocalDateTime joinTime;

    @Schema(description = "数据来源")
    private String source;

    @Schema(description = "联系人")
    private String contactPerson;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
