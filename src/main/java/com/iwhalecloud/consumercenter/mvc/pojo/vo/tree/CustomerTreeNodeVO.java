package com.iwhalecloud.consumercenter.mvc.pojo.vo.tree;

import lombok.Data;

import java.util.List;

/**
 * 客户树节点VO
 */
@Data
public class CustomerTreeNodeVO {

    private Long id;

    private String nodeName;

    private Long parentId;

    private Integer level;

    private Integer sortOrder;

    private String nodeType;

    private String responsibility;

    private String managerName;

    private Integer customerCount;

    private List<CustomerTreeNodeVO> children;
}
