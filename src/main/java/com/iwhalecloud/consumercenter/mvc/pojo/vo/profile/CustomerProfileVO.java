package com.iwhalecloud.consumercenter.mvc.pojo.vo.profile;

import com.iwhalecloud.consumercenter.mvc.pojo.vo.decision.DecisionChainVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.tree.CustomerTreeNodeVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 客户360度画像VO
 */
@Data
@Schema(description = "客户360度画像")
public class CustomerProfileVO {

    @Schema(description = "基础信息")
    private BasicInfo basicInfo;

    @Schema(description = "看管信息")
    private ManagementInfo managementInfo;

    @Schema(description = "业务数据")
    private BusinessInfo businessInfo;

    @Schema(description = "分支列表")
    private List<CustomerBranchVO> branches;

    @Schema(description = "交互记录")
    private List<InteractionRecord> interactionRecords;

    @Schema(description = "决策链")
    private List<DecisionChainVO> decisionChains;

    @Schema(description = "政策动态")
    private List<PolicyVO> policies;

    @Schema(description = "历史订单/项目")
    private List<CustomerOrderVO> orders;

    @Schema(description = "使用过的产品与方案")
    private List<CustomerProductVO> products;

    @Data
    @Schema(description = "基础信息")
    public static class BasicInfo {
        @Schema(description = "客户ID")
        private Long customerId;
        @Schema(description = "客户编号")
        private String customerCode;
        @Schema(description = "客户名称")
        private String customerName;
        @Schema(description = "英文名称")
        private String customerNameEn;
        @Schema(description = "所属行业")
        private String industry;
        @Schema(description = "所属区域")
        private String region;
        @Schema(description = "企业规模")
        private String scale;
        @Schema(description = "联系人")
        private String contactPerson;
        @Schema(description = "联系电话")
        private String contactPhone;
        @Schema(description = "地址")
        private String address;
    }

    @Data
    @Schema(description = "看管信息")
    public static class ManagementInfo {
        @Schema(description = "看管状态")
        private String statusDesc;
        @Schema(description = "客户经理")
        private String managerName;
        @Schema(description = "入库时间")
        private String joinTime;
        @Schema(description = "所属客户树节点")
        private List<CustomerTreeNodeVO> treeNodes;
    }

    @Data
    @Schema(description = "业务数据")
    public static class BusinessInfo {
        @Schema(description = "招标次数")
        private Integer bidCount;
        @Schema(description = "招标总金额(万元)")
        private BigDecimal bidAmount;
        @Schema(description = "信息化招标数量")
        private Integer itBidCount;
        @Schema(description = "订单数量")
        private Integer orderCount;
        @Schema(description = "订单总金额(万元)")
        private BigDecimal orderAmount;
        @Schema(description = "收入贡献(万元)")
        private Long revenueContribution;
        @Schema(description = "合作时长(年)")
        private Integer cooperationYears;
        @Schema(description = "使用产品/方案数量")
        private Integer productCount;
    }

    @Data
    @Schema(description = "交互记录")
    public static class InteractionRecord {
        @Schema(description = "记录ID")
        private Long id;
        @Schema(description = "交互类型")
        private String type;
        @Schema(description = "交互内容")
        private String content;
        @Schema(description = "参与人")
        private String participant;
        @Schema(description = "交互时间")
        private String time;
        @Schema(description = "交互结果")
        private String result;
    }
}
