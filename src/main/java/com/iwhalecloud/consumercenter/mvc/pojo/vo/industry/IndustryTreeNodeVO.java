package com.iwhalecloud.consumercenter.mvc.pojo.vo.industry;

import lombok.Data;

import java.util.List;

/**
 * 行业树节点VO
 */
@Data
public class IndustryTreeNodeVO {

    private Long id;

    private String nodeName;

    private Long parentId;

    private Integer level;

    private Integer sortOrder;

    private String nodeCode;

    private String description;

    private Integer customerCount;

    private List<IndustryTreeNodeVO> children;
}
