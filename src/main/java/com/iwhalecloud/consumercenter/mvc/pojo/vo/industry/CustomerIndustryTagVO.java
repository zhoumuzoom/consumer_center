package com.iwhalecloud.consumercenter.mvc.pojo.vo.industry;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户行业标签VO
 */
@Data
@Schema(description = "客户行业标签信息")
public class CustomerIndustryTagVO {

    @Schema(description = "标签ID")
    private Long id;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "行业节点ID")
    private Long industryNodeId;

    @Schema(description = "行业名称")
    private String industryName;

    @Schema(description = "标签级别")
    private String tagLevel;

    @Schema(description = "合作状态")
    private String cooperationStatus;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
