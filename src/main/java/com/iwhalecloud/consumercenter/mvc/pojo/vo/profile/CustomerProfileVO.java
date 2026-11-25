package com.iwhalecloud.consumercenter.mvc.pojo.vo.profile;

import com.iwhalecloud.consumercenter.mvc.pojo.vo.decision.DecisionChainVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.tree.CustomerTreeNodeVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 客户360度画像VO
 */
@Data
public class CustomerProfileVO {

    private BasicInfo basicInfo;

    private ManagementInfo managementInfo;

    private BusinessInfo businessInfo;

    private List<CustomerBranchVO> branches;

    private List<InteractionRecord> interactionRecords;

    private List<DecisionChainVO> decisionChains;

    private List<PolicyVO> policies;

    private List<CustomerOrderVO> orders;

    private List<CustomerProductVO> products;

    @Data
    public static class BasicInfo {
        private Long customerId;
        private String customerCode;
        private String customerName;
        private String customerNameEn;
        private String industry;
        private String region;
        private String scale;
        private String contactPerson;
        private String contactPhone;
        private String address;
    }

    @Data
    public static class ManagementInfo {
        private String statusDesc;
        private String managerName;
        private String joinTime;
        private List<CustomerTreeNodeVO> treeNodes;
    }

    @Data
    public static class BusinessInfo {
        private Integer bidCount;
        private BigDecimal bidAmount;
        private Integer itBidCount;
        private Integer orderCount;
        private BigDecimal orderAmount;
        private Long revenueContribution;
        private Integer cooperationYears;
        private Integer productCount;
    }

    @Data
    public static class InteractionRecord {
        private Long id;
        private String type;
        private String content;
        private String participant;
        private String time;
        private String result;
    }
}
