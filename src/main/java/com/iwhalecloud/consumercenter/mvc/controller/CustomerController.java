package com.iwhalecloud.consumercenter.mvc.controller;

import com.iwhalecloud.consumercenter.common.result.PageResult;
import com.iwhalecloud.consumercenter.common.result.Result;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerCreateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerExportDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerQueryDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerStatusUpdateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerUpdateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.customer.CustomerDetailVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.customer.CustomerExportVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.customer.CustomerVO;
import com.iwhalecloud.consumercenter.mvc.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

/**
 * 客户档案管理
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public Result<PageResult<CustomerVO>> pageList(CustomerQueryDTO queryDTO) {
        return Result.success(customerService.pageList(queryDTO));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(CustomerExportDTO exportDTO) {
        CustomerExportVO exportVO = customerService.export(exportDTO);
        if (exportVO.getTotal() == null || exportVO.getTotal() == 0) {
            return ResponseEntity.noContent().build();
        }
        String contentDisposition = ContentDisposition.attachment()
                .filename(exportVO.getFilename(), StandardCharsets.UTF_8)
                .build()
                .toString();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .header("X-Export-Count", String.valueOf(exportVO.getTotal()))
                .cacheControl(CacheControl.noStore())
                .body(exportVO.getContent());
    }

    @GetMapping("/{id}")
    public Result<CustomerDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(customerService.getDetail(id));
    }

    @PostMapping
    public Result<CustomerVO> create(@Valid @RequestBody CustomerCreateDTO createDTO) {
        return Result.success(customerService.create(createDTO));
    }

    @PutMapping("/{id}")
    public Result<CustomerVO> update(@PathVariable Long id, @Valid @RequestBody CustomerUpdateDTO updateDTO) {
        updateDTO.setId(id);
        return Result.success(customerService.update(updateDTO));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<CustomerVO> updateStatus(@PathVariable Long id, @Valid @RequestBody CustomerStatusUpdateDTO statusDTO) {
        return Result.success(customerService.updateStatus(id, statusDTO));
    }
}
