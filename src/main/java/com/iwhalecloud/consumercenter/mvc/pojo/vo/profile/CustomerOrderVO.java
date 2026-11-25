package com.iwhalecloud.consumercenter.mvc.pojo.vo.profile;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CustomerOrderVO {
    private Long id;
    private Long customerId;
    private String orderNo;
    private String projectName;
    private String projectType;
    private BigDecimal orderAmount;
    private LocalDateTime contractDate;
    private LocalDateTime deliveryDate;
    private String orderStatus;
    private String description;
}
