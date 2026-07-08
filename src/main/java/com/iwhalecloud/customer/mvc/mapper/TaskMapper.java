package com.iwhalecloud.customer.mvc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iwhalecloud.customer.mvc.pojo.entity.Task;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务表 Mapper
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {
}