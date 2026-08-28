package com.iwhalecloud.consumercenter.mvc.pojo.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "新增产品/方案请求")
public class CustomerProductCreateDTO {
    @NotBlank(message = "产品/方案名称不能为空")
    @Schema(description = "产品/方案名称")
    private String productName;

    @Schema(description = "类型(产品/解决方案)")
    private String productType;

    @Schema(description = "分类(云计算/大数据/AI/安全/网络/办公/其他)")
    private String category;

    @Schema(description = "使用状态(使用中/已停用/评估中)")
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
