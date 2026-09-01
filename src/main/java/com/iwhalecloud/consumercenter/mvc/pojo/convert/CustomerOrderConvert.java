package com.iwhalecloud.consumercenter.mvc.pojo.convert;

import com.iwhalecloud.consumercenter.mvc.pojo.dto.profile.CustomerOrderCreateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerOrder;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.profile.CustomerOrderVO;

/**
 * 客户订单对象转换
 */
public final class CustomerOrderConvert {

    private CustomerOrderConvert() {
    }

    public static CustomerOrder dto2entity(CustomerOrderCreateDTO source) {
        CustomerOrder target = new CustomerOrder();
        target.setProjectName(source.getProjectName());
        target.setOrderNo(source.getOrderNo());
        target.setProjectType(source.getProjectType());
        target.setOrderAmount(source.getOrderAmount());
        target.setContractDate(source.getContractDate());
        target.setDeliveryDate(source.getDeliveryDate());
        target.setOrderStatus(source.getOrderStatus());
        target.setDescription(source.getDescription());
        return target;
    }

    public static CustomerOrderVO entity2vo(CustomerOrder source) {
        CustomerOrderVO target = new CustomerOrderVO();
        target.setId(source.getId());
        target.setCustomerId(source.getCustomerId());
        target.setOrderNo(source.getOrderNo());
        target.setProjectName(source.getProjectName());
        target.setProjectType(source.getProjectType());
        target.setOrderAmount(source.getOrderAmount());
        target.setContractDate(source.getContractDate());
        target.setDeliveryDate(source.getDeliveryDate());
        target.setOrderStatus(source.getOrderStatus());
        target.setDescription(source.getDescription());
        return target;
    }
}
