package com.iwhalecloud.consumercenter.mvc.pojo.convert;

import com.iwhalecloud.consumercenter.mvc.pojo.dto.tree.CustomerTreeNodeCreateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.tree.CustomerTreeNodeUpdateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerTreeNode;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.tree.CustomerTreeNodeVO;

/**
 * 客户树节点对象转换
 */
public final class CustomerTreeNodeConvert {

    private CustomerTreeNodeConvert() {
    }

    public static CustomerTreeNode dto2entity(CustomerTreeNodeCreateDTO source) {
        CustomerTreeNode target = new CustomerTreeNode();
        target.setNodeName(source.getNodeName());
        target.setParentId(source.getParentId());
        target.setNodeType(source.getNodeType());
        target.setResponsibility(source.getResponsibility());
        target.setManagerName(source.getManagerName());
        return target;
    }

    public static void dto2entity(CustomerTreeNodeUpdateDTO source, CustomerTreeNode target) {
        target.setId(source.getId());
        target.setNodeName(source.getNodeName());
        target.setNodeType(source.getNodeType());
        target.setResponsibility(source.getResponsibility());
        target.setManagerName(source.getManagerName());
        target.setSortOrder(source.getSortOrder());
    }

    public static CustomerTreeNodeVO entity2vo(CustomerTreeNode source) {
        CustomerTreeNodeVO target = new CustomerTreeNodeVO();
        target.setId(source.getId());
        target.setNodeName(source.getNodeName());
        target.setParentId(source.getParentId());
        target.setLevel(source.getLevel());
        target.setSortOrder(source.getSortOrder());
        target.setNodeType(source.getNodeType());
        target.setResponsibility(source.getResponsibility());
        target.setManagerName(source.getManagerName());
        target.setCustomerCount(source.getCustomerCount());
        return target;
    }
}
