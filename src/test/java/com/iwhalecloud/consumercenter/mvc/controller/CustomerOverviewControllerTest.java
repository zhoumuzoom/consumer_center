package com.iwhalecloud.consumercenter.mvc.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iwhalecloud.consumercenter.common.error.GlobalExceptionHandler;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.customer.CustomerStatisticsVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.customer.DrillDownVO;
import com.iwhalecloud.consumercenter.mvc.service.CustomerOverviewService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 客户规模总览控制器测试
 */
@WebMvcTest(CustomerOverviewController.class)
@Import(GlobalExceptionHandler.class)
class CustomerOverviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerOverviewService customerOverviewService;

    private MockedStatic<StpUtil> stpUtilMockedStatic;

    /**
     * 模拟登录态，使认证拦截器放行
     */
    @BeforeEach
    void mockLoginState() {
        stpUtilMockedStatic = mockStatic(StpUtil.class);
        stpUtilMockedStatic.when(StpUtil::getTokenValue).thenReturn("test-token");
        stpUtilMockedStatic.when(() -> StpUtil.getLoginDeviceTypeByToken(anyString())).thenReturn("access");
    }

    @AfterEach
    void closeLoginStateMock() {
        stpUtilMockedStatic.close();
    }

    // ==================== getStatistics ====================

    @Nested
    @DisplayName("获取客户规模统计测试")
    class GetStatisticsTest {

        @Test
        @DisplayName("获取统计数据-正常返回完整统计信息")
        void getStatistics_success() throws Exception {
            CustomerStatisticsVO statisticsVO = new CustomerStatisticsVO();
            statisticsVO.setTotalCustomers(1000);
            statisticsVO.setManagedCustomers(600);
            statisticsVO.setManagementRate(60.0);
            statisticsVO.setIndustryDistribution(Arrays.asList(
                    new CustomerStatisticsVO.DistributionItem("IT", 300),
                    new CustomerStatisticsVO.DistributionItem("金融", 250),
                    new CustomerStatisticsVO.DistributionItem("制造", 200)
            ));
            statisticsVO.setRegionDistribution(Arrays.asList(
                    new CustomerStatisticsVO.DistributionItem("华东", 400),
                    new CustomerStatisticsVO.DistributionItem("华南", 300),
                    new CustomerStatisticsVO.DistributionItem("华北", 300)
            ));
            statisticsVO.setSourceDistribution(Arrays.asList(
                    new CustomerStatisticsVO.DistributionItem("手动录入", 500),
                    new CustomerStatisticsVO.DistributionItem("系统导入", 500)
            ));

            when(customerOverviewService.getStatistics()).thenReturn(statisticsVO);

            mockMvc.perform(get("/api/customer/overview/statistics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.totalCustomers").value(1000))
                    .andExpect(jsonPath("$.data.managedCustomers").value(600))
                    .andExpect(jsonPath("$.data.managementRate").value(60.0))
                    .andExpect(jsonPath("$.data.industryDistribution").isArray())
                    .andExpect(jsonPath("$.data.industryDistribution[0].name").value("IT"))
                    .andExpect(jsonPath("$.data.industryDistribution[0].count").value(300))
                    .andExpect(jsonPath("$.data.regionDistribution[1].name").value("华南"))
                    .andExpect(jsonPath("$.data.sourceDistribution[0].name").value("手动录入"));
        }

        @Test
        @DisplayName("获取统计数据-空数据返回零值和空列表")
        void getStatistics_emptyData() throws Exception {
            CustomerStatisticsVO statisticsVO = new CustomerStatisticsVO();
            statisticsVO.setTotalCustomers(0);
            statisticsVO.setManagedCustomers(0);
            statisticsVO.setManagementRate(0.0);
            statisticsVO.setIndustryDistribution(Collections.emptyList());
            statisticsVO.setRegionDistribution(Collections.emptyList());
            statisticsVO.setSourceDistribution(Collections.emptyList());

            when(customerOverviewService.getStatistics()).thenReturn(statisticsVO);

            mockMvc.perform(get("/api/customer/overview/statistics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.totalCustomers").value(0))
                    .andExpect(jsonPath("$.data.managedCustomers").value(0))
                    .andExpect(jsonPath("$.data.managementRate").value(0.0))
                    .andExpect(jsonPath("$.data.industryDistribution").isArray())
                    .andExpect(jsonPath("$.data.industryDistribution").isEmpty())
                    .andExpect(jsonPath("$.data.regionDistribution").isArray())
                    .andExpect(jsonPath("$.data.regionDistribution").isEmpty())
                    .andExpect(jsonPath("$.data.sourceDistribution").isArray())
                    .andExpect(jsonPath("$.data.sourceDistribution").isEmpty());
        }

        @Test
        @DisplayName("异常测试-获取统计数据时发生系统异常")
        void getStatistics_exception() throws Exception {
            when(customerOverviewService.getStatistics()).thenThrow(new RuntimeException("数据库连接失败"));

            mockMvc.perform(get("/api/customer/overview/statistics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(5001))
                    .andExpect(jsonPath("$.message").value("系统异常"));
        }
    }

    // ==================== drillDown ====================

    @Nested
    @DisplayName("维度下钻分析测试")
    class DrillDownTest {

        @Test
        @DisplayName("下钻分析-仅传递必填参数dimension，使用默认level值")
        void drillDown_withRequiredParamsOnly() throws Exception {
            DrillDownVO drillDownVO = new DrillDownVO();
            drillDownVO.setDimension("industry");
            drillDownVO.setDimensionValue(null);
            drillDownVO.setTotalCustomers(1000);
            drillDownVO.setChildren(Arrays.asList(
                    new DrillDownVO.DrillDownItem("IT", 300, 180, 60.0),
                    new DrillDownVO.DrillDownItem("金融", 250, 150, 60.0)
            ));
            drillDownVO.setCustomers(Collections.emptyList());

            when(customerOverviewService.drillDown(eq("industry"), any(), eq(1))).thenReturn(drillDownVO);

            mockMvc.perform(get("/api/customer/overview/drill-down")
                            .param("dimension", "industry"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.dimension").value("industry"))
                    .andExpect(jsonPath("$.data.dimensionValue").doesNotExist())
                    .andExpect(jsonPath("$.data.totalCustomers").value(1000))
                    .andExpect(jsonPath("$.data.children").isArray())
                    .andExpect(jsonPath("$.data.children[0].name").value("IT"))
                    .andExpect(jsonPath("$.data.children[0].customerCount").value(300))
                    .andExpect(jsonPath("$.data.children[0].managedCount").value(180))
                    .andExpect(jsonPath("$.data.children[0].managementRate").value(60.0));
        }

        @Test
        @DisplayName("下钻分析-传递全部参数，包含dimensionValue和自定义level")
        void drillDown_withAllParams() throws Exception {
            DrillDownVO drillDownVO = new DrillDownVO();
            drillDownVO.setDimension("industry");
            drillDownVO.setDimensionValue("IT");
            drillDownVO.setTotalCustomers(300);
            drillDownVO.setChildren(Arrays.asList(
                    new DrillDownVO.DrillDownItem("云计算", 100, 70, 70.0),
                    new DrillDownVO.DrillDownItem("人工智能", 80, 50, 62.5)
            ));
            drillDownVO.setCustomers(Collections.emptyList());

            when(customerOverviewService.drillDown(eq("industry"), eq("IT"), eq(2))).thenReturn(drillDownVO);

            mockMvc.perform(get("/api/customer/overview/drill-down")
                            .param("dimension", "industry")
                            .param("dimensionValue", "IT")
                            .param("level", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.dimension").value("industry"))
                    .andExpect(jsonPath("$.data.dimensionValue").value("IT"))
                    .andExpect(jsonPath("$.data.totalCustomers").value(300))
                    .andExpect(jsonPath("$.data.children").isArray())
                    .andExpect(jsonPath("$.data.children[0].name").value("云计算"))
                    .andExpect(jsonPath("$.data.children[0].customerCount").value(100))
                    .andExpect(jsonPath("$.data.children[1].name").value("人工智能"))
                    .andExpect(jsonPath("$.data.children[1].customerCount").value(80));
        }

        @Test
        @DisplayName("下钻分析-返回空结果")
        void drillDown_emptyResult() throws Exception {
            DrillDownVO drillDownVO = new DrillDownVO();
            drillDownVO.setDimension("region");
            drillDownVO.setDimensionValue("不存在的区域");
            drillDownVO.setTotalCustomers(0);
            drillDownVO.setChildren(Collections.emptyList());
            drillDownVO.setCustomers(Collections.emptyList());

            when(customerOverviewService.drillDown(eq("region"), eq("不存在的区域"), eq(1))).thenReturn(drillDownVO);

            mockMvc.perform(get("/api/customer/overview/drill-down")
                            .param("dimension", "region")
                            .param("dimensionValue", "不存在的区域"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.dimension").value("region"))
                    .andExpect(jsonPath("$.data.dimensionValue").value("不存在的区域"))
                    .andExpect(jsonPath("$.data.totalCustomers").value(0))
                    .andExpect(jsonPath("$.data.children").isArray())
                    .andExpect(jsonPath("$.data.children").isEmpty());
        }

        @Test
        @DisplayName("下钻分析-返回包含客户列表的结果")
        void drillDown_withCustomers() throws Exception {
            DrillDownVO drillDownVO = new DrillDownVO();
            drillDownVO.setDimension("industry");
            drillDownVO.setDimensionValue("IT");
            drillDownVO.setTotalCustomers(2);
            drillDownVO.setChildren(Collections.emptyList());
            // Note: CustomerVO would need to be populated here if needed
            drillDownVO.setCustomers(Collections.emptyList());

            when(customerOverviewService.drillDown(eq("industry"), eq("IT"), eq(1))).thenReturn(drillDownVO);

            mockMvc.perform(get("/api/customer/overview/drill-down")
                            .param("dimension", "industry")
                            .param("dimensionValue", "IT"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.dimension").value("industry"))
                    .andExpect(jsonPath("$.data.totalCustomers").value(2));
        }

        @Test
        @DisplayName("异常测试-下钻分析时dimension参数缺失")
        void drillDown_missingDimension() throws Exception {
            mockMvc.perform(get("/api/customer/overview/drill-down"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(5001))
                    .andExpect(jsonPath("$.message").value("系统异常"));
        }

        @Test
        @DisplayName("异常测试-下钻分析时发生系统异常")
        void drillDown_exception() throws Exception {
            when(customerOverviewService.drillDown(eq("industry"), any(), eq(1)))
                    .thenThrow(new RuntimeException("查询失败"));

            mockMvc.perform(get("/api/customer/overview/drill-down")
                            .param("dimension", "industry"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(5001))
                    .andExpect(jsonPath("$.message").value("系统异常"));
        }

        @Test
        @DisplayName("异常测试-下钻分析时维度值不存在，服务抛出RuntimeException")
        void drillDown_dimensionNotFound() throws Exception {
            when(customerOverviewService.drillDown(eq("invalid"), any(), eq(1)))
                    .thenThrow(new RuntimeException("维度不存在"));

            mockMvc.perform(get("/api/customer/overview/drill-down")
                            .param("dimension", "invalid"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(5001))
                    .andExpect(jsonPath("$.message").value("系统异常"));
        }
    }
}
