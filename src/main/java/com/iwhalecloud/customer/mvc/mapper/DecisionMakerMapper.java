package com.iwhalecloud.customer.mvc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iwhalecloud.customer.mvc.pojo.entity.DecisionMaker;
import org.apache.ibatis.annotations.Mapper;

/**
 * 决策关键人表 Mapper
 */
@Mapper
public interface DecisionMakerMapper extends BaseMapper<DecisionMaker> {
}