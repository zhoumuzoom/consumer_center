package com.iwhalecloud.customer.mvc.pojo.vo.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 客户统计VO
 */
@Data
@Schema(description = "客户统计信息")
public class CustomerStatisticsVO {

    @Schema(description = "客户总量")
    private Integer totalCustomers;

    @Schema(description = "已看管客户数量")
    private Integer managedCustomers;

    @Schema(description = "客户看管率(%)")
    private Double managementRate;

    @Schema(description = "行业分布")
    private List<DistributionItem> industryDistribution;

    @Schema(description = "区域分布")
    private List<DistributionItem> regionDistribution;

    @Schema(description = "数据来源分布")
    private List<DistributionItem> sourceDistribution;

    @Data
    @Schema(description = "分布项")
    public static class DistributionItem {
        @Schema(description = "名称")
        private String name;
        @Schema(description = "数量")
        private Integer count;

        public DistributionItem() {}

        public DistributionItem(String name, Integer count) {
            this.name = name;
            this.count = count;
        }
    }
}
