package com.iwhalecloud.consumercenter.mvc.pojo.vo.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "历史订单/项目VO")
public class CustomerOrderVO {
    @Schema(description = "ID")
    private Long id;
    @Schema(description = "客户ID")
    private Long customerId;
    @Schema(description = "订单编号")
    private String orderNo;
    @Schema(description = "项目名称")
    private String projectName;
    @Schema(description = "项目类型")
    private String projectType;
    @Schema(description = "订单金额(万元)")
    private BigDecimal orderAmount;
    @Schema(description = "签约日期")
    private LocalDateTime contractDate;
    @Schema(description = "交付日期")
    private LocalDateTime deliveryDate;
    @Schema(description = "订单状态")
    private String orderStatus;
    @Schema(description = "项目描述")
    private String description;
}
