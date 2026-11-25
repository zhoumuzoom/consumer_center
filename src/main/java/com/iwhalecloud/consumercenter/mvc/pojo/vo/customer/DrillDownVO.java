package com.iwhalecloud.consumercenter.mvc.pojo.vo.customer;

import lombok.Data;

import java.util.List;

/**
 * 下钻分析VO
 */
@Data
public class DrillDownVO {

    private String dimension;

    private String dimensionValue;

    private List<DrillDownItem> children;

    private List<CustomerVO> customers;

    private Integer totalCustomers;

    @Data
    public static class DrillDownItem {
        private String name;
        private Integer customerCount;
        private Integer managedCount;
        private Double managementRate;

        public DrillDownItem() {}

        public DrillDownItem(String name, Integer customerCount, Integer managedCount, Double managementRate) {
            this.name = name;
            this.customerCount = customerCount;
            this.managedCount = managedCount;
            this.managementRate = managementRate;
        }
    }
}
