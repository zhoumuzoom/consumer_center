package com.iwhalecloud.consumercenter.mvc.pojo.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PolicyCreateDTO {
    @NotBlank(message = "政策标题不能为空")
    private String policyTitle;

    private String policyType;

    private String policyLevel;

    private String issuingAuthority;

    private LocalDateTime publishDate;

    private LocalDateTime effectiveDate;

    private LocalDateTime expireDate;

    private String policySummary;

    private String impactAnalysis;

    private String sourceUrl;
}
