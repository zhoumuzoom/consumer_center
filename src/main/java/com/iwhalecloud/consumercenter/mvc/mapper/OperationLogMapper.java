package com.iwhalecloud.consumercenter.mvc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志表 Mapper
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}