package com.iwhalecloud.customer.mvc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iwhalecloud.customer.common.result.PageResult;
import com.iwhalecloud.customer.mvc.mapper.CustomerMapper;
import com.iwhalecloud.customer.mvc.pojo.dto.customer.CustomerCreateDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.customer.CustomerQueryDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.customer.CustomerStatusUpdateDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.customer.CustomerUpdateDTO;
import com.iwhalecloud.customer.mvc.pojo.entity.Customer;
import com.iwhalecloud.customer.mvc.pojo.vo.customer.CustomerDetailVO;
import com.iwhalecloud.customer.mvc.pojo.vo.customer.CustomerVO;
import com.iwhalecloud.customer.mvc.service.CustomerService;
import com.iwhalecloud.customer.mvc.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 客户服务实现
 */
@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private OperationLogService operationLogService;

    @Override
    public PageResult<CustomerVO> pageList(CustomerQueryDTO queryDTO) {
        Page<Customer> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();

        // 构建查询条件
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.and(w -> w.like(Customer::getCustomerName, queryDTO.getKeyword())
                    .or().like(Customer::getCustomerCode, queryDTO.getKeyword()));
        }
        if (StringUtils.hasText(queryDTO.getIndustry())) {
            wrapper.eq(Customer::getIndustry, queryDTO.getIndustry());
        }
        if (StringUtils.hasText(queryDTO.getRegion())) {
            wrapper.eq(Customer::getRegion, queryDTO.getRegion());
        }
        if (StringUtils.hasText(queryDTO.getScale())) {
            wrapper.eq(Customer::getScale, queryDTO.getScale());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Customer::getStatus, queryDTO.getStatus());
        }
        if (StringUtils.hasText(queryDTO.getSource())) {
            wrapper.eq(Customer::getSource, queryDTO.getSource());
        }

        wrapper.orderByDesc(Customer::getCreateTime);

        Page<Customer> result = customerMapper.selectPage(page, wrapper);

        List<CustomerVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        PageResult<CustomerVO> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setPages(result.getPages());
        pageResult.setCurrent(result.getCurrent());
        pageResult.setSize(result.getSize());
        pageResult.setRecords(voList);

        return pageResult;
    }

    @Override
    public CustomerDetailVO getDetail(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new RuntimeException("客户不存在");
        }

        CustomerDetailVO detailVO = new CustomerDetailVO();
        BeanUtils.copyProperties(convertToVO(customer), detailVO);
        detailVO.setContactEmail(customer.getContactEmail());
        detailVO.setAddress(customer.getAddress());
        detailVO.setRemark(customer.getRemark());
        // Demo阶段设置模拟数据
        detailVO.setBranchCount(3);
        detailVO.setDecisionMakerCount(2);
        detailVO.setInteractionCount(5);

        return detailVO;
    }

    @Override
    public CustomerVO create(CustomerCreateDTO createDTO) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(createDTO, customer);

        // 生成客户编号
        customer.setCustomerCode(generateCustomerCode());
        customer.setStatus((byte) 0); // 默认未看管
        customer.setJoinTime(LocalDateTime.now());
        customer.setSource("手动录入");

        customerMapper.insert(customer);

        operationLogService.log(customer.getId(), "customer", "CREATE",
                "新增客户: " + customer.getCustomerName());

        return convertToVO(customer);
    }

    @Override
    public CustomerVO update(CustomerUpdateDTO updateDTO) {
        Customer customer = customerMapper.selectById(updateDTO.getId());
        if (customer == null) {
            throw new RuntimeException("客户不存在");
        }

        String beforeData = customer.toString();
        BeanUtils.copyProperties(updateDTO, customer);
        customerMapper.updateById(customer);

        operationLogService.log(customer.getId(), "customer", "UPDATE",
                "修改客户: " + customer.getCustomerName(), beforeData, customer.toString());

        return convertToVO(customer);
    }

    @Override
    public void delete(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new RuntimeException("客户不存在");
        }

        customerMapper.deleteById(id);

        operationLogService.log(id, "customer", "DELETE",
                "删除客户: " + customer.getCustomerName());
    }

    @Override
    public CustomerVO updateStatus(Long id, CustomerStatusUpdateDTO statusDTO) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new RuntimeException("客户不存在");
        }

        Byte oldStatus = customer.getStatus();
        customer.setStatus(statusDTO.getStatus() != null ? statusDTO.getStatus().byteValue() : null);
        customerMapper.updateById(customer);

        String statusDesc = getStatusDesc(statusDTO.getStatus() != null ? statusDTO.getStatus().byteValue() : null);
        operationLogService.log(id, "customer", "STATUS_CHANGE",
                "客户状态变更: " + getStatusDesc(oldStatus) + " -> " + statusDesc
                        + (statusDTO.getReason() != null ? ", 原因: " + statusDTO.getReason() : ""));

        return convertToVO(customer);
    }

    private CustomerVO convertToVO(Customer customer) {
        CustomerVO vo = new CustomerVO();
        BeanUtils.copyProperties(customer, vo);
        vo.setStatusDesc(getStatusDesc(customer.getStatus()));
        return vo;
    }

    private String getStatusDesc(Byte status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "未看管";
            case 1 -> "已看管";
            case 2 -> "已剔除";
            default -> "未知";
        };
    }

    private String generateCustomerCode() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "CUS" + timestamp + random;
    }
}
