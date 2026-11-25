package com.iwhalecloud.consumercenter.mvc.pojo.dto.profile;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomerBranchUpdateDTO {
    @NotNull(message = "分支ID不能为空")
    private Long id;

    private String branchName;
    private String region;
    private String address;
    private String contactPerson;
    private String contactPhone;
    private String businessScope;
    private String branchType;
    private Integer employeeCount;
}
