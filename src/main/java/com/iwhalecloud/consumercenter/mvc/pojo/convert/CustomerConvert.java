package com.iwhalecloud.consumercenter.mvc.pojo.convert;

import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerCreateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerUpdateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.Customer;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.customer.CustomerDetailVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.customer.CustomerVO;

/**
 * 客户对象转换
 */
public final class CustomerConvert {

    private CustomerConvert() {
    }

    public static Customer dto2entity(CustomerCreateDTO source) {
        Customer target = new Customer();
        target.setCustomerName(source.getCustomerName());
        target.setCustomerNameEn(source.getCustomerNameEn());
        target.setIndustry(source.getIndustry());
        target.setRegion(source.getRegion());
        target.setScale(source.getScale());
        target.setContactPerson(source.getContactPerson());
        target.setContactPhone(source.getContactPhone());
        target.setContactEmail(source.getContactEmail());
        target.setAddress(source.getAddress());
        target.setRemark(source.getRemark());
        return target;
    }

    public static void dto2entity(CustomerUpdateDTO source, Customer target) {
        target.setId(source.getId());
        target.setCustomerName(source.getCustomerName());
        target.setCustomerNameEn(source.getCustomerNameEn());
        target.setIndustry(source.getIndustry());
        target.setRegion(source.getRegion());
        target.setScale(source.getScale());
        target.setContactPerson(source.getContactPerson());
        target.setContactPhone(source.getContactPhone());
        target.setContactEmail(source.getContactEmail());
        target.setAddress(source.getAddress());
        target.setRemark(source.getRemark());
    }

    public static CustomerVO entity2vo(Customer source) {
        CustomerVO target = new CustomerVO();
        target.setId(source.getId());
        target.setCustomerCode(source.getCustomerCode());
        target.setCustomerName(source.getCustomerName());
        target.setCustomerNameEn(source.getCustomerNameEn());
        target.setIndustry(source.getIndustry());
        target.setRegion(source.getRegion());
        target.setScale(source.getScale());
        target.setStatus(source.getStatus() == null ? null : source.getStatus().intValue());
        target.setManagerName(source.getManagerName());
        target.setJoinTime(source.getJoinTime());
        target.setSource(source.getSource());
        target.setContactPerson(source.getContactPerson());
        target.setContactPhone(source.getContactPhone());
        target.setCreateTime(source.getCreateTime());
        target.setStatusDesc(statusDesc(source.getStatus()));
        return target;
    }

    public static CustomerDetailVO entity2detailVO(Customer source) {
        CustomerVO base = entity2vo(source);
        CustomerDetailVO target = new CustomerDetailVO();
        target.setId(base.getId());
        target.setCustomerCode(base.getCustomerCode());
        target.setCustomerName(base.getCustomerName());
        target.setCustomerNameEn(base.getCustomerNameEn());
        target.setIndustry(base.getIndustry());
        target.setRegion(base.getRegion());
        target.setScale(base.getScale());
        target.setStatus(base.getStatus());
        target.setStatusDesc(base.getStatusDesc());
        target.setManagerName(base.getManagerName());
        target.setJoinTime(base.getJoinTime());
        target.setSource(base.getSource());
        target.setContactPerson(base.getContactPerson());
        target.setContactPhone(base.getContactPhone());
        target.setCreateTime(base.getCreateTime());
        target.setContactEmail(source.getContactEmail());
        target.setAddress(source.getAddress());
        target.setRemark(source.getRemark());
        return target;
    }

    private static String statusDesc(Byte status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case 0 -> "未看管";
            case 1 -> "已看管";
            case 2 -> "已剔除";
            default -> "未知";
        };
    }
}
