package com.iwhalecloud.consumercenter.mvc.service;

import com.iwhalecloud.consumercenter.mvc.pojo.vo.customer.CustomerStatisticsVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.customer.DrillDownVO;

/**
 * 客户规模总览服务
 */
public interface CustomerOverviewService {

    /**
     * 获取客户规模统计
     */
    CustomerStatisticsVO getStatistics();

    /**
     * 维度下钻分析
     */
    DrillDownVO drillDown(String dimension, String dimensionValue, Integer level);
}
