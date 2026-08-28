package com.iwhalecloud.consumercenter.mvc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户历史订单/项目表 Mapper
 */
@Mapper
public interface CustomerOrderMapper extends BaseMapper<CustomerOrder> {
}