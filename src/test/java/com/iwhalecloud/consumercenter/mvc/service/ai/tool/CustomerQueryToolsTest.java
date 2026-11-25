package com.iwhalecloud.consumercenter.mvc.service.ai.tool;

import com.iwhalecloud.consumercenter.common.result.PageResult;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerQueryDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.customer.CustomerVO;
import com.iwhalecloud.consumercenter.mvc.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 客户查询工具测试
 */
@ExtendWith(MockitoExtension.class)
class CustomerQueryToolsTest {

    @InjectMocks
    private CustomerQueryTools customerQueryTools;

    @Mock
    private CustomerService customerService;

    @Test
    @DisplayName("查询客户返回脱敏摘要并限制10条")
    void searchCustomers_returnsMaskedSummary() {
        CustomerVO customer = new CustomerVO();
        customer.setId(1L);
        customer.setCustomerName("中国工商银行");
        customer.setIndustry("金融业");
        customer.setRegion("华北");
        customer.setScale("大型");
        customer.setStatusDesc("已看管");
        customer.setManagerName("张三");
        customer.setContactPhone("13800000000");

        PageResult<CustomerVO> pageResult = new PageResult<>();
        pageResult.setTotal(1L);
        pageResult.setRecords(Collections.singletonList(customer));
        when(customerService.pageList(any(CustomerQueryDTO.class))).thenReturn(pageResult);

        String result = customerQueryTools.searchCustomers("银行", null, null, null, null);

        assertTrue(result.contains("中国工商银行"));
        assertTrue(result.contains("金融业"));
        assertFalse(result.contains("13800000000"));
        ArgumentCaptor<CustomerQueryDTO> captor = ArgumentCaptor.forClass(CustomerQueryDTO.class);
        verify(customerService).pageList(captor.capture());
        assertTrue(captor.getValue().getSize() <= 10);
    }
}