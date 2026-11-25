package com.iwhalecloud.consumercenter.mvc.pojo.vo.customer;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户VO
 */
@Data
public class CustomerVO {

    private Long id;

    private String customerCode;

    private String customerName;

    private String customerNameEn;

    private String industry;

    private String region;

    private String scale;

    private Integer status;

    private String statusDesc;

    private String managerName;

    private LocalDateTime joinTime;

    private String source;

    private String contactPerson;

    private String contactPhone;

    private LocalDateTime createTime;
}
