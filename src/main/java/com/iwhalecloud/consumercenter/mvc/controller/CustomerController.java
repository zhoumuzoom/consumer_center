package com.iwhalecloud.consumercenter.mvc.controller;

import com.iwhalecloud.consumercenter.common.result.PageResult;
import com.iwhalecloud.consumercenter.common.result.Result;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerCreateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerQueryDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerStatusUpdateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerUpdateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.customer.CustomerDetailVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.customer.CustomerVO;
import com.iwhalecloud.consumercenter.mvc.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 客户档案管理
 */
@Tag(name = "客户档案管理", description = "客户CRUD及状态管理")
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Operation(summary = "客户列表查询")
    @GetMapping
    public Result<PageResult<CustomerVO>> pageList(CustomerQueryDTO queryDTO) {
        return Result.success(customerService.pageList(queryDTO));
    }

    @Operation(summary = "客户详情查询")
    @GetMapping("/{id}")
    public Result<CustomerDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(customerService.getDetail(id));
    }

    @Operation(summary = "新增客户")
    @PostMapping
    public Result<CustomerVO> create(@Valid @RequestBody CustomerCreateDTO createDTO) {
        return Result.success(customerService.create(createDTO));
    }

    @Operation(summary = "修改客户")
    @PutMapping("/{id}")
    public Result<CustomerVO> update(@PathVariable Long id, @Valid @RequestBody CustomerUpdateDTO updateDTO) {
        updateDTO.setId(id);
        return Result.success(customerService.update(updateDTO));
    }

    @Operation(summary = "删除客户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return Result.success();
    }

    @Operation(summary = "客户看管状态维护")
    @PutMapping("/{id}/status")
    public Result<CustomerVO> updateStatus(@PathVariable Long id, @Valid @RequestBody CustomerStatusUpdateDTO statusDTO) {
        return Result.success(customerService.updateStatus(id, statusDTO));
    }
}
