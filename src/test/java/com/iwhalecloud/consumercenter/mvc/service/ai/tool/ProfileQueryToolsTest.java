package com.iwhalecloud.consumercenter.mvc.service.ai.tool;

import com.iwhalecloud.consumercenter.common.result.PageResult;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerQueryDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.customer.CustomerVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.profile.CustomerBranchVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.profile.CustomerOrderVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.profile.CustomerProfileVO;
import com.iwhalecloud.consumercenter.mvc.service.CustomerProfileService;
import com.iwhalecloud.consumercenter.mvc.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * 客户画像工具测试
 */
@ExtendWith(MockitoExtension.class)
class ProfileQueryToolsTest {

    @InjectMocks
    private ProfileQueryTools profileQueryTools;

    @Mock
    private CustomerService customerService;

    @Mock
    private CustomerProfileService customerProfileService;

    @Test
    @DisplayName("按名称查询画像返回摘要且不含联系方式")
    void getCustomerProfile_returnsSummaryWithoutContacts() {
        CustomerVO customer = new CustomerVO();
        customer.setId(1L);
        customer.setCustomerName("中国工商银行");
        PageResult<CustomerVO> pageResult = new PageResult<>();
        pageResult.setRecords(Collections.singletonList(customer));
        when(customerService.pageList(any(CustomerQueryDTO.class))).thenReturn(pageResult);

        CustomerProfileVO profile = new CustomerProfileVO();
        CustomerProfileVO.BasicInfo basicInfo = new CustomerProfileVO.BasicInfo();
        basicInfo.setCustomerId(1L);
        basicInfo.setCustomerName("中国工商银行");
        basicInfo.setIndustry("金融业");
        basicInfo.setContactPhone("13800000000");
        profile.setBasicInfo(basicInfo);

        CustomerProfileVO.BusinessInfo businessInfo = new CustomerProfileVO.BusinessInfo();
        businessInfo.setOrderCount(5);
        businessInfo.setOrderAmount(new BigDecimal("5310"));
        profile.setBusinessInfo(businessInfo);

        CustomerBranchVO branch = new CustomerBranchVO();
        branch.setBranchName("北京总行");
        branch.setRegion("华北");
        branch.setContactPhone("13900000000");
        CustomerBranchVO childBranch = new CustomerBranchVO();
        childBranch.setBranchName("上海分行");
        childBranch.setRegion("华东");
        branch.setChildren(Collections.singletonList(childBranch));
        profile.setBranches(Collections.singletonList(branch));

        CustomerOrderVO order = new CustomerOrderVO();
        order.setProjectName("核心银行系统升级");
        order.setOrderStatus("执行中");
        profile.setOrders(Collections.singletonList(order));

        when(customerProfileService.getProfile(eq(1L))).thenReturn(profile);

        String result = profileQueryTools.getCustomerProfile("中国工商银行");

        assertTrue(result.contains("中国工商银行"));
        assertTrue(result.contains("核心银行系统升级"));
        assertTrue(result.contains("上海分行"));
        assertFalse(result.contains("13800000000"));
        assertFalse(result.contains("13900000000"));
    }

    @Test
    @DisplayName("客户不存在时返回可读提示")
    void getCustomerProfile_whenNotFound_returnsFriendlyMessage() {
        PageResult<CustomerVO> pageResult = new PageResult<>();
        pageResult.setRecords(Collections.emptyList());
        when(customerService.pageList(any(CustomerQueryDTO.class))).thenReturn(pageResult);

        String result = profileQueryTools.getCustomerProfile("不存在的客户");

        assertTrue(result.contains("未找到"));
    }
}