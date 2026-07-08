package com.iwhalecloud.customer.mvc.pojo.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "新增客户分支请求")
public class CustomerBranchCreateDTO {
    @NotBlank(message = "分支名称不能为空")
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

    @Schema(description = "父分支ID，0为总部直属")
    private Long parentBranchId = 0L;

    @Schema(description = "分支类型(总部/海外分支/本地实体)")
    private String branchType;

    @Schema(description = "员工人数")
    private Integer employeeCount;
}
