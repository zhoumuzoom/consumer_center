package com.iwhalecloud.consumercenter.mvc.pojo.vo.profile;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CustomerProductVO {
    private Long id;
    private Long customerId;
    private String productName;
    private String productType;
    private String category;
    private String usageStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal annualFee;
    private String description;
}
