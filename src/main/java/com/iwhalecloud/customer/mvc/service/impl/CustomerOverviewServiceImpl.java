package com.iwhalecloud.customer.mvc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iwhalecloud.customer.mvc.mapper.CustomerMapper;
import com.iwhalecloud.customer.mvc.pojo.entity.Customer;
import com.iwhalecloud.customer.mvc.pojo.vo.customer.CustomerStatisticsVO;
import com.iwhalecloud.customer.mvc.pojo.vo.customer.CustomerVO;
import com.iwhalecloud.customer.mvc.pojo.vo.customer.DrillDownVO;
import com.iwhalecloud.customer.mvc.service.CustomerOverviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 客户规模总览服务实现
 */
@Service
public class CustomerOverviewServiceImpl implements CustomerOverviewService {

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public CustomerStatisticsVO getStatistics() {
        List<Customer> allCustomers = customerMapper.selectList(new LambdaQueryWrapper<>());

        CustomerStatisticsVO statistics = new CustomerStatisticsVO();
        statistics.setTotalCustomers(allCustomers.size());

        long managedCount = allCustomers.stream()
                .filter(c -> c.getStatus() != null && c.getStatus() == (byte) 1)
                .count();
        statistics.setManagedCustomers((int) managedCount);

        double rate = allCustomers.isEmpty() ? 0 : (managedCount * 100.0 / allCustomers.size());
        statistics.setManagementRate(Math.round(rate * 100.0) / 100.0);

        // 行业分布
        Map<String, Long> industryMap = allCustomers.stream()
                .filter(c -> c.getIndustry() != null)
                .collect(Collectors.groupingBy(Customer::getIndustry, Collectors.counting()));
        statistics.setIndustryDistribution(
                industryMap.entrySet().stream()
                        .map(e -> new CustomerStatisticsVO.DistributionItem(e.getKey(), e.getValue().intValue()))
                        .sorted((a, b) -> b.getCount() - a.getCount())
                        .collect(Collectors.toList())
        );

        // 区域分布
        Map<String, Long> regionMap = allCustomers.stream()
                .filter(c -> c.getRegion() != null)
                .collect(Collectors.groupingBy(Customer::getRegion, Collectors.counting()));
        statistics.setRegionDistribution(
                regionMap.entrySet().stream()
                        .map(e -> new CustomerStatisticsVO.DistributionItem(e.getKey(), e.getValue().intValue()))
                        .sorted((a, b) -> b.getCount() - a.getCount())
                        .collect(Collectors.toList())
        );

        // 数据来源分布
        Map<String, Long> sourceMap = allCustomers.stream()
                .filter(c -> c.getSource() != null)
                .collect(Collectors.groupingBy(Customer::getSource, Collectors.counting()));
        statistics.setSourceDistribution(
                sourceMap.entrySet().stream()
                        .map(e -> new CustomerStatisticsVO.DistributionItem(e.getKey(), e.getValue().intValue()))
                        .sorted((a, b) -> b.getCount() - a.getCount())
                        .collect(Collectors.toList())
        );

        return statistics;
    }

    @Override
    public DrillDownVO drillDown(String dimension, String dimensionValue, Integer level) {
        DrillDownVO drillDown = new DrillDownVO();
        drillDown.setDimension(dimension);
        drillDown.setDimensionValue(dimensionValue);

        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        if ("industry".equals(dimension) && dimensionValue != null) {
            wrapper.eq(Customer::getIndustry, dimensionValue);
        } else if ("region".equals(dimension) && dimensionValue != null) {
            wrapper.eq(Customer::getRegion, dimensionValue);
        }

        List<Customer> filteredCustomers = customerMapper.selectList(wrapper);
        drillDown.setTotalCustomers(filteredCustomers.size());

        // 构建子维度
        List<DrillDownVO.DrillDownItem> children = new ArrayList<>();
        if ("industry".equals(dimension)) {
            // 按规模细分
            Map<String, List<Customer>> scaleMap = filteredCustomers.stream()
                    .filter(c -> c.getScale() != null)
                    .collect(Collectors.groupingBy(Customer::getScale));
            for (Map.Entry<String, List<Customer>> entry : scaleMap.entrySet()) {
                long managed = entry.getValue().stream().filter(c -> c.getStatus() != null && c.getStatus() == (byte) 1).count();
                double rate = entry.getValue().isEmpty() ? 0 : (managed * 100.0 / entry.getValue().size());
                children.add(new DrillDownVO.DrillDownItem(entry.getKey(), entry.getValue().size(), (int) managed, Math.round(rate * 100.0) / 100.0));
            }
        } else if ("region".equals(dimension)) {
            // 按行业细分
            Map<String, List<Customer>> indMap = filteredCustomers.stream()
                    .filter(c -> c.getIndustry() != null)
                    .collect(Collectors.groupingBy(Customer::getIndustry));
            for (Map.Entry<String, List<Customer>> entry : indMap.entrySet()) {
                long managed = entry.getValue().stream().filter(c -> c.getStatus() != null && c.getStatus() == (byte) 1).count();
                double rate = entry.getValue().isEmpty() ? 0 : (managed * 100.0 / entry.getValue().size());
                children.add(new DrillDownVO.DrillDownItem(entry.getKey(), entry.getValue().size(), (int) managed, Math.round(rate * 100.0) / 100.0));
            }
        }
        drillDown.setChildren(children);

        // 设置客户列表（最多返回50条）
        List<CustomerVO> customerVOs = filteredCustomers.stream()
                .limit(50)
                .map(c -> {
                    CustomerVO vo = new CustomerVO();
                    vo.setId(c.getId());
                    vo.setCustomerCode(c.getCustomerCode());
                    vo.setCustomerName(c.getCustomerName());
                    vo.setIndustry(c.getIndustry());
                    vo.setRegion(c.getRegion());
                    vo.setScale(c.getScale());
                    vo.setStatus(c.getStatus() != null ? c.getStatus().intValue() : null);
                    vo.setStatusDesc(getStatusDesc(c.getStatus()));
                    vo.setManagerName(c.getManagerName());
                    vo.setJoinTime(c.getJoinTime());
                    vo.setSource(c.getSource());
                    return vo;
                })
                .collect(Collectors.toList());
        drillDown.setCustomers(customerVOs);

        return drillDown;
    }

    private String getStatusDesc(Byte status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "未看管";
            case 1 -> "已看管";
            case 2 -> "已剔除";
            default -> "未知";
        };
    }
}
