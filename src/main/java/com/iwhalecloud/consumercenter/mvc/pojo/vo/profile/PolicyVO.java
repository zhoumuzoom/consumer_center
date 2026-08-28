package com.iwhalecloud.consumercenter.mvc.pojo.vo.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "政策动态VO")
public class PolicyVO {
    @Schema(description = "ID")
    private Long id;
    @Schema(description = "客户ID")
    private Long customerId;
    @Schema(description = "政策标题")
    private String policyTitle;
    @Schema(description = "政策类型")
    private String policyType;
    @Schema(description = "政策级别")
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
    @Schema(description = "影响分析")
    private String impactAnalysis;
    @Schema(description = "来源链接")
    private String sourceUrl;
    @Schema(description = "状态")
    private String status;
}
