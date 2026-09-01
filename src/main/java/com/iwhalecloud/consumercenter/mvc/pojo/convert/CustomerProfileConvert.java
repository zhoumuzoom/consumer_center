package com.iwhalecloud.consumercenter.mvc.pojo.convert;

import com.iwhalecloud.consumercenter.mvc.pojo.entity.Customer;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.profile.CustomerProfileVO;

/**
 * 客户画像对象转换
 */
public final class CustomerProfileConvert {

    private CustomerProfileConvert() {
    }

    public static CustomerProfileVO.BasicInfo entity2basicInfo(Customer source) {
        CustomerProfileVO.BasicInfo target = new CustomerProfileVO.BasicInfo();
        target.setCustomerId(source.getId());
        target.setCustomerCode(source.getCustomerCode());
        target.setCustomerName(source.getCustomerName());
        target.setCustomerNameEn(source.getCustomerNameEn());
        target.setIndustry(source.getIndustry());
        target.setRegion(source.getRegion());
        target.setScale(source.getScale());
        target.setContactPerson(source.getContactPerson());
        target.setContactPhone(source.getContactPhone());
        target.setAddress(source.getAddress());
        return target;
    }
}
