package com.iwhalecloud.consumercenter.mvc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerProduct;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户产品与方案表 Mapper
 */
@Mapper
public interface CustomerProductMapper extends BaseMapper<CustomerProduct> {
}