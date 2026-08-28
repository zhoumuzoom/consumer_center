package com.iwhalecloud.consumercenter.mvc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerPolicy;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户政策动态表 Mapper
 */
@Mapper
public interface CustomerPolicyMapper extends BaseMapper<CustomerPolicy> {
}