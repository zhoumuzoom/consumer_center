package com.iwhalecloud.consumercenter.mvc.pojo.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerBranchCreateDTO {
    @NotBlank(message = "分支名称不能为空")
    private String branchName;

    private String region;

    private String address;

    private String contactPerson;

    private String contactPhone;

    private String businessScope;

    private Long parentBranchId = 0L;

    private String branchType;

    private Integer employeeCount;
}
