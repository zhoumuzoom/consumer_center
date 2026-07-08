package com.iwhalecloud.customer.mvc.pojo.vo.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "客户分支信息")
public class CustomerBranchVO {
    @Schema(description = "分支ID")
    private Long id;
    @Schema(description = "客户ID")
    private Long customerId;
    @Schema(description = "分支名称")
    private String branchName;
    @Schema(description = "所在区域")
    private String region;
    @Schema(description = "详细地址")
    private String address;
    @Schema(description = "联系人")
    private String contactPerson;
    @Schema(description = "联系电话")
    private String contactPhone;
    @Schema(description = "业务范围")
    private String businessScope;
    @Schema(description = "父分支ID")
    private Long parentBranchId;
    @Schema(description = "分支类型(总部/海外分支/本地实体)")
    private String branchType;
    @Schema(description = "员工人数")
    private Integer employeeCount;
    @Schema(description = "子分支列表")
    private List<CustomerBranchVO> children;
    @Schema(description = "业务贡献(万元)")
    private Long businessContribution;
    @Schema(description = "贡献占比(%)")
    private Double contributionRate;
}
