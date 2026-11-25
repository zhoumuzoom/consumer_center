package com.iwhalecloud.consumercenter.mvc.pojo.vo.customer;

import lombok.Data;

import java.util.List;

/**
 * 客户统计VO
 */
@Data
public class CustomerStatisticsVO {

    private Integer totalCustomers;

    private Integer managedCustomers;

    private Double managementRate;

    private List<DistributionItem> industryDistribution;

    private List<DistributionItem> regionDistribution;

    private List<DistributionItem> sourceDistribution;

    @Data
    public static class DistributionItem {
        private String name;
        private Integer count;

        public DistributionItem() {}

        public DistributionItem(String name, Integer count) {
            this.name = name;
            this.count = count;
        }
    }
}
