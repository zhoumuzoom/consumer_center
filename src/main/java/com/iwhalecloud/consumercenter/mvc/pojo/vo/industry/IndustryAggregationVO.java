package com.iwhalecloud.consumercenter.mvc.pojo.vo.industry;

import lombok.Data;

/**
 * 行业聚合数据VO
 */
@Data
public class IndustryAggregationVO {

    private Long nodeId;

    private String nodeName;

    private Integer customerCount;

    private Double managementRate;

    private Integer groupCount;

    private Integer strategicCount;

    private Integer keyCount;
}
