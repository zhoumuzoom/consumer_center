package com.iwhalecloud.customer.mvc.pojo.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "新增政策动态请求")
public class PolicyCreateDTO {
    @NotBlank(message = "政策标题不能为空")
    @Schema(description = "政策标题")
    private String policyTitle;

    @Schema(description = "政策类型(产业/财税/贸易/金融/其他)")
    private String policyType;

    @Schema(description = "政策级别(国家/省级/市级/行业)")
    private String policyLevel;

    @Schema(description = "发布机构")
    private String issuingAuthority;

    @Schema(description = "发布日期")
    private LocalDateTime publishDate;

    @Schema(description = "生效日期")
    private LocalDateTime effectiveDate;

    @Schema(description = "失效日期")
    private LocalDateTime expireDate;

    @Schema(description = "政策摘要")
    private String policySummary;

    @Schema(description = "对客户的影响分析")
    private String impactAnalysis;

    @Schema(description = "政策来源链接")
    private String sourceUrl;
}
