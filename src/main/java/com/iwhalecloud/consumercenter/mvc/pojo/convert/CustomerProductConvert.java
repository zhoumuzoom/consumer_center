package com.iwhalecloud.consumercenter.mvc.pojo.convert;

import com.iwhalecloud.consumercenter.mvc.pojo.dto.profile.CustomerProductCreateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerProduct;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.profile.CustomerProductVO;

/**
 * 客户产品对象转换
 */
public final class CustomerProductConvert {

    private CustomerProductConvert() {
    }

    public static CustomerProduct dto2entity(CustomerProductCreateDTO source) {
        CustomerProduct target = new CustomerProduct();
        target.setProductName(source.getProductName());
        target.setProductType(source.getProductType());
        target.setCategory(source.getCategory());
        target.setUsageStatus(source.getUsageStatus());
        target.setStartDate(source.getStartDate());
        target.setEndDate(source.getEndDate());
        target.setAnnualFee(source.getAnnualFee());
        target.setDescription(source.getDescription());
        return target;
    }

    public static CustomerProductVO entity2vo(CustomerProduct source) {
        CustomerProductVO target = new CustomerProductVO();
        target.setId(source.getId());
        target.setCustomerId(source.getCustomerId());
        target.setProductName(source.getProductName());
        target.setProductType(source.getProductType());
        target.setCategory(source.getCategory());
        target.setUsageStatus(source.getUsageStatus());
        target.setStartDate(source.getStartDate());
        target.setEndDate(source.getEndDate());
        target.setAnnualFee(source.getAnnualFee());
        target.setDescription(source.getDescription());
        return target;
    }
}
