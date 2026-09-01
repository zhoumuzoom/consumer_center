package com.iwhalecloud.consumercenter.mvc.pojo.convert;

import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerIndustryTag;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.industry.CustomerIndustryTagVO;

/**
 * 客户行业标签对象转换
 */
public final class CustomerIndustryTagConvert {

    private CustomerIndustryTagConvert() {
    }

    public static CustomerIndustryTagVO entity2vo(CustomerIndustryTag source) {
        CustomerIndustryTagVO target = new CustomerIndustryTagVO();
        target.setId(source.getId());
        target.setCustomerId(source.getCustomerId());
        target.setIndustryNodeId(source.getIndustryNodeId());
        target.setTagLevel(source.getTagLevel());
        target.setCooperationStatus(source.getCooperationStatus());
        target.setCreateTime(source.getCreateTime());
        return target;
    }
}
