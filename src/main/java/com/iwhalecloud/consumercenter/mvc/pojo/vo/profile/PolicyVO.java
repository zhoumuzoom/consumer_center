package com.iwhalecloud.consumercenter.mvc.pojo.vo.profile;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PolicyVO {
    private Long id;
    private Long customerId;
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
    private String status;
}
