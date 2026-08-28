package com.iwhalecloud.consumercenter.mvc.pojo.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "新增历史订单/项目请求")
public class CustomerOrderCreateDTO {
    @NotBlank(message = "项目名称不能为空")
    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "项目类型(信息化/基建/咨询/运维/其他)")
    private String projectType;

    @Schema(description = "订单金额(万元)")
    private BigDecimal orderAmount;

    @Schema(description = "签约日期")
    private LocalDateTime contractDate;

    @Schema(description = "交付日期")
    private LocalDateTime deliveryDate;

    @Schema(description = "订单状态(洽谈中/已签约/执行中/已交付/已验收/已终止)")
    private String orderStatus;

    @Schema(description = "项目描述")
    private String description;
}
