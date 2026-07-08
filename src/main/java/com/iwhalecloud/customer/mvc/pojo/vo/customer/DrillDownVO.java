package com.iwhalecloud.customer.mvc.pojo.vo.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 下钻分析VO
 */
@Data
@Schema(description = "下钻分析结果")
public class DrillDownVO {

    @Schema(description = "维度类型")
    private String dimension;

    @Schema(description = "维度值")
    private String dimensionValue;

    @Schema(description = "子维度列表")
    private List<DrillDownItem> children;

    @Schema(description = "该维度下的客户列表")
    private List<CustomerVO> customers;

    @Schema(description = "客户总数")
    private Integer totalCustomers;

    @Data
    @Schema(description = "下钻子项")
    public static class DrillDownItem {
        @Schema(description = "子维度名称")
        private String name;
        @Schema(description = "客户数量")
        private Integer customerCount;
        @Schema(description = "已看管数量")
        private Integer managedCount;
        @Schema(description = "看管率(%)")
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
