package com.iwhalecloud.consumercenter.mvc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.SystemUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户表 Mapper
 */
@Mapper
public interface SystemUserMapper extends BaseMapper<SystemUser> {
}