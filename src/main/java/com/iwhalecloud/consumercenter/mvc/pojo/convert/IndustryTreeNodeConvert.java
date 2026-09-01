package com.iwhalecloud.consumercenter.mvc.pojo.convert;

import com.iwhalecloud.consumercenter.mvc.pojo.dto.industry.IndustryTreeNodeCreateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.IndustryTreeNode;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.industry.IndustryTreeNodeVO;

/**
 * 行业树节点对象转换
 */
public final class IndustryTreeNodeConvert {

    private IndustryTreeNodeConvert() {
    }

    public static IndustryTreeNode dto2entity(IndustryTreeNodeCreateDTO source) {
        IndustryTreeNode target = new IndustryTreeNode();
        target.setNodeName(source.getNodeName());
        target.setParentId(source.getParentId());
        target.setNodeCode(source.getNodeCode());
        target.setDescription(source.getDescription());
        target.setSortOrder(source.getSortOrder());
        return target;
    }

    public static IndustryTreeNodeVO entity2vo(IndustryTreeNode source) {
        IndustryTreeNodeVO target = new IndustryTreeNodeVO();
        target.setId(source.getId());
        target.setNodeName(source.getNodeName());
        target.setParentId(source.getParentId());
        target.setLevel(source.getLevel());
        target.setSortOrder(source.getSortOrder());
        target.setNodeCode(source.getNodeCode());
        target.setDescription(source.getDescription());
        return target;
    }
}
