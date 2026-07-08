package com.iwhalecloud.customer.mvc.pojo.vo.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "产品与方案VO")
public class CustomerProductVO {
    @Schema(description = "ID")
    private Long id;
    @Schema(description = "客户ID")
    private Long customerId;
    @Schema(description = "产品/方案名称")
    private String productName;
    @Schema(description = "类型(产品/解决方案)")
    private String productType;
    @Schema(description = "分类")
    private String category;
    @Schema(description = "使用状态")
    private String usageStatus;
    @Schema(description = "启用日期")
    private LocalDateTime startDate;
    @Schema(description = "停用日期")
    private LocalDateTime endDate;
    @Schema(description = "年费(万元)")
    private BigDecimal annualFee;
    @Schema(description = "描述")
    private String description;
}
