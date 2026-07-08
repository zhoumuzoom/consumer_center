package com.iwhalecloud.customer.mvc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iwhalecloud.customer.mvc.pojo.entity.IndustryTreeNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 行业树节点表 Mapper
 */
@Mapper
public interface IndustryTreeNodeMapper extends BaseMapper<IndustryTreeNode> {
}