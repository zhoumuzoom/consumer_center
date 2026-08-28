package com.iwhalecloud.consumercenter.mvc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.DecisionChain;
import org.apache.ibatis.annotations.Mapper;

/**
 * 决策链关系表 Mapper
 */
@Mapper
public interface DecisionChainMapper extends BaseMapper<DecisionChain> {
}