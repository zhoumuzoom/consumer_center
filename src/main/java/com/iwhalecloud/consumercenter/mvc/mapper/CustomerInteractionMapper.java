package com.iwhalecloud.consumercenter.mvc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerInteraction;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户交互记录表 Mapper
 */
@Mapper
public interface CustomerInteractionMapper extends BaseMapper<CustomerInteraction> {
}