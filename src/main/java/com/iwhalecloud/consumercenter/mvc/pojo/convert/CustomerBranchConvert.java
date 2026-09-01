package com.iwhalecloud.consumercenter.mvc.pojo.convert;

import com.iwhalecloud.consumercenter.mvc.pojo.dto.profile.CustomerBranchCreateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.profile.CustomerBranchUpdateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerBranch;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.profile.CustomerBranchVO;

/**
 * 客户分支对象转换
 */
public final class CustomerBranchConvert {

    private CustomerBranchConvert() {
    }

    public static CustomerBranch dto2entity(CustomerBranchCreateDTO source) {
        CustomerBranch target = new CustomerBranch();
        target.setBranchName(source.getBranchName());
        target.setRegion(source.getRegion());
        target.setAddress(source.getAddress());
        target.setContactPerson(source.getContactPerson());
        target.setContactPhone(source.getContactPhone());
        target.setBusinessScope(source.getBusinessScope());
        target.setParentBranchId(source.getParentBranchId());
        target.setBranchType(source.getBranchType());
        target.setEmployeeCount(source.getEmployeeCount());
        return target;
    }

    public static void dto2entity(CustomerBranchUpdateDTO source, CustomerBranch target) {
        target.setId(source.getId());
        target.setBranchName(source.getBranchName());
        target.setRegion(source.getRegion());
        target.setAddress(source.getAddress());
        target.setContactPerson(source.getContactPerson());
        target.setContactPhone(source.getContactPhone());
        target.setBusinessScope(source.getBusinessScope());
        target.setBranchType(source.getBranchType());
        target.setEmployeeCount(source.getEmployeeCount());
    }

    public static CustomerBranchVO entity2vo(CustomerBranch source) {
        CustomerBranchVO target = new CustomerBranchVO();
        target.setId(source.getId());
        target.setCustomerId(source.getCustomerId());
        target.setBranchName(source.getBranchName());
        target.setRegion(source.getRegion());
        target.setAddress(source.getAddress());
        target.setContactPerson(source.getContactPerson());
        target.setContactPhone(source.getContactPhone());
        target.setBusinessScope(source.getBusinessScope());
        target.setParentBranchId(source.getParentBranchId());
        target.setBranchType(source.getBranchType());
        target.setEmployeeCount(source.getEmployeeCount());
        return target;
    }
}
