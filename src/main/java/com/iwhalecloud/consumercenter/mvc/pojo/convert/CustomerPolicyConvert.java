package com.iwhalecloud.consumercenter.mvc.pojo.convert;

import com.iwhalecloud.consumercenter.mvc.pojo.dto.profile.PolicyCreateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerPolicy;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.profile.PolicyVO;

/**
 * 客户政策对象转换
 */
public final class CustomerPolicyConvert {

    private CustomerPolicyConvert() {
    }

    public static CustomerPolicy dto2entity(PolicyCreateDTO source) {
        CustomerPolicy target = new CustomerPolicy();
        target.setPolicyTitle(source.getPolicyTitle());
        target.setPolicyType(source.getPolicyType());
        target.setPolicyLevel(source.getPolicyLevel());
        target.setIssuingAuthority(source.getIssuingAuthority());
        target.setPublishDate(source.getPublishDate());
        target.setEffectiveDate(source.getEffectiveDate());
        target.setExpireDate(source.getExpireDate());
        target.setPolicySummary(source.getPolicySummary());
        target.setImpactAnalysis(source.getImpactAnalysis());
        target.setSourceUrl(source.getSourceUrl());
        return target;
    }

    public static PolicyVO entity2vo(CustomerPolicy source) {
        PolicyVO target = new PolicyVO();
        target.setId(source.getId());
        target.setCustomerId(source.getCustomerId());
        target.setPolicyTitle(source.getPolicyTitle());
        target.setPolicyType(source.getPolicyType());
        target.setPolicyLevel(source.getPolicyLevel());
        target.setIssuingAuthority(source.getIssuingAuthority());
        target.setPublishDate(source.getPublishDate());
        target.setEffectiveDate(source.getEffectiveDate());
        target.setExpireDate(source.getExpireDate());
        target.setPolicySummary(source.getPolicySummary());
        target.setImpactAnalysis(source.getImpactAnalysis());
        target.setSourceUrl(source.getSourceUrl());
        target.setStatus(source.getStatus());
        return target;
    }
}
