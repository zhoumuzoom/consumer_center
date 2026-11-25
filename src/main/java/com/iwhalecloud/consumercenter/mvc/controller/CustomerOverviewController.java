package com.iwhalecloud.consumercenter.mvc.controller;

import com.iwhalecloud.consumercenter.common.result.Result;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.customer.CustomerStatisticsVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.customer.DrillDownVO;
import com.iwhalecloud.consumercenter.mvc.service.CustomerOverviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客户规模总览
 */
@RestController
@RequestMapping("/api/customer/overview")
public class CustomerOverviewController {

    @Autowired
    private CustomerOverviewService customerOverviewService;

    @GetMapping("/statistics")
    public Result<CustomerStatisticsVO> getStatistics() {
        return Result.success(customerOverviewService.getStatistics());
    }

    @GetMapping("/drill-down")
    public Result<DrillDownVO> drillDown(
            @RequestParam String dimension,
            @RequestParam(required = false) String dimensionValue,
            @RequestParam(required = false, defaultValue = "1") Integer level) {
        return Result.success(customerOverviewService.drillDown(dimension, dimensionValue, level));
    }
}
