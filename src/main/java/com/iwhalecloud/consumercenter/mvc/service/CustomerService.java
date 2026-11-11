package com.iwhalecloud.consumercenter.mvc.service;

import com.iwhalecloud.consumercenter.common.result.PageResult;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerCreateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerExportDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerQueryDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerStatusUpdateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerUpdateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.customer.CustomerDetailVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.customer.CustomerExportVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.customer.CustomerVO;

/**
 * 客户服务
 */
public interface CustomerService {

    /**
     * 分页查询客户列表
     */
    PageResult<CustomerVO> pageList(CustomerQueryDTO queryDTO);

    /**
     * 导出客户列表
     */
    CustomerExportVO export(CustomerExportDTO exportDTO);

    /**
     * 查询客户详情
     */
    CustomerDetailVO getDetail(Long id);

    /**
     * 新增客户
     */
    CustomerVO create(CustomerCreateDTO createDTO);

    /**
     * 修改客户
     */
    CustomerVO update(CustomerUpdateDTO updateDTO);

    /**
     * 删除客户
     */
    void delete(Long id);

    /**
     * 更新客户看管状态
     */
    CustomerVO updateStatus(Long id, CustomerStatusUpdateDTO statusDTO);
}
