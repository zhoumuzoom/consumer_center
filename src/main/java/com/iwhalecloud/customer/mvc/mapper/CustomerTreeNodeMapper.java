package com.iwhalecloud.customer.mvc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iwhalecloud.customer.mvc.pojo.entity.CustomerTreeNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户树节点表 Mapper
 */
@Mapper
public interface CustomerTreeNodeMapper extends BaseMapper<CustomerTreeNode> {
}