package com.iwhalecloud.customer.mvc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iwhalecloud.customer.mvc.pojo.entity.ReportScheduleConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时上报配置表 Mapper
 */
@Mapper
public interface ReportScheduleConfigMapper extends BaseMapper<ReportScheduleConfig> {
}