package com.iwhalecloud.consumercenter.mvc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerBranch;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户分支表 Mapper
 */
@Mapper
public interface CustomerBranchMapper extends BaseMapper<CustomerBranch> {
}