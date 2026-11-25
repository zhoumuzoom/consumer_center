package com.iwhalecloud.consumercenter.mvc.pojo.vo.tree;

import lombok.Data;

/**
 * 客户树节点统计VO
 */
@Data
public class CustomerTreeStatisticsVO {

    private Long nodeId;

    private String nodeName;

    private Integer customerCount;

    private Integer childNodeCount;

    private Double managementRate;
}
