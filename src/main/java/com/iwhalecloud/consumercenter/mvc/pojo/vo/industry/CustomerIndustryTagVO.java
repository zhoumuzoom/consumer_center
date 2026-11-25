package com.iwhalecloud.consumercenter.mvc.pojo.vo.industry;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户行业标签VO
 */
@Data
public class CustomerIndustryTagVO {

    private Long id;

    private Long customerId;

    private String customerName;

    private Long industryNodeId;

    private String industryName;

    private String tagLevel;

    private String cooperationStatus;

    private LocalDateTime createTime;
}
