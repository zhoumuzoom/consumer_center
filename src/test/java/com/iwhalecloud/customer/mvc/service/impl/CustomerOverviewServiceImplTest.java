package com.iwhalecloud.customer.mvc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iwhalecloud.customer.mvc.mapper.CustomerMapper;
import com.iwhalecloud.customer.mvc.pojo.entity.Customer;
import com.iwhalecloud.customer.mvc.pojo.vo.customer.CustomerStatisticsVO;
import com.iwhalecloud.customer.mvc.pojo.vo.customer.DrillDownVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 客户规模总览服务测试
 */
@ExtendWith(MockitoExtension.class)
class CustomerOverviewServiceImplTest {

    @InjectMocks
    private CustomerOverviewServiceImpl customerOverviewService;

    @Mock
    private CustomerMapper customerMapper;

    // ==================== getStatistics ====================

    @Nested
    @DisplayName("获取客户规模统计测试")
    class GetStatisticsTest {

        @Test
        @DisplayName("空客户列表-返回零值和空分布")
        void getStatistics_emptyList() {
            when(customerMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

            CustomerStatisticsVO result = customerOverviewService.getStatistics();

            assertNotNull(result);
            assertEquals(0, result.getTotalCustomers());
            assertEquals(0, result.getManagedCustomers());
            assertEquals(0.0, result.getManagementRate());
            assertNotNull(result.getIndustryDistribution());
            assertTrue(result.getIndustryDistribution().isEmpty());
            assertNotNull(result.getRegionDistribution());
            assertTrue(result.getRegionDistribution().isEmpty());
            assertNotNull(result.getSourceDistribution());
            assertTrue(result.getSourceDistribution().isEmpty());
            verify(customerMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("非空客户列表-包含已看管和未看管客户")
        void getStatistics_withMixedStatus() {
            Customer customer1 = buildCustomer(1L, "IT", "华东", "大型", (byte) 1, "手动录入");
            Customer customer2 = buildCustomer(2L, "IT", "华东", "中型", (byte) 0, "手动录入");
            Customer customer3 = buildCustomer(3L, "金融", "华南", "小型", (byte) 1, "系统导入");
            Customer customer4 = buildCustomer(4L, "制造", "华北", "大型", null, "139系统");

            when(customerMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(customer1, customer2, customer3, customer4));

            CustomerStatisticsVO result = customerOverviewService.getStatistics();

            assertNotNull(result);
            assertEquals(4, result.getTotalCustomers());
            assertEquals(2, result.getManagedCustomers());
            assertEquals(50.0, result.getManagementRate());
            verify(customerMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("客户列表包含null行业-行业分布过滤null值")
        void getStatistics_withNullIndustry() {
            Customer customer1 = buildCustomer(1L, "IT", "华东", "大型", (byte) 1, "手动录入");
            Customer customer2 = buildCustomer(2L, null, "华东", "中型", (byte) 0, "手动录入");
            Customer customer3 = buildCustomer(3L, "金融", "华南", "小型", (byte) 1, "系统导入");

            when(customerMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(customer1, customer2, customer3));

            CustomerStatisticsVO result = customerOverviewService.getStatistics();

            assertNotNull(result);
            assertEquals(3, result.getTotalCustomers());
            assertEquals(2, result.getIndustryDistribution().size());
            // IT和金融各1个，null被过滤
            assertEquals("IT", result.getIndustryDistribution().get(0).getName());
            assertEquals(1, result.getIndustryDistribution().get(0).getCount());
            verify(customerMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("客户列表包含null区域-区域分布过滤null值")
        void getStatistics_withNullRegion() {
            Customer customer1 = buildCustomer(1L, "IT", "华东", "大型", (byte) 1, "手动录入");
            Customer customer2 = buildCustomer(2L, "IT", null, "中型", (byte) 0, "手动录入");
            Customer customer3 = buildCustomer(3L, "金融", "华南", "小型", (byte) 1, "系统导入");

            when(customerMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(customer1, customer2, customer3));

            CustomerStatisticsVO result = customerOverviewService.getStatistics();

            assertNotNull(result);
            assertEquals(3, result.getTotalCustomers());
            assertEquals(2, result.getRegionDistribution().size());
            // 华东和华南各1个，null被过滤
            verify(customerMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("客户列表包含null数据来源-来源分布过滤null值")
        void getStatistics_withNullSource() {
            Customer customer1 = buildCustomer(1L, "IT", "华东", "大型", (byte) 1, "手动录入");
            Customer customer2 = buildCustomer(2L, "IT", "华东", "中型", (byte) 0, null);
            Customer customer3 = buildCustomer(3L, "金融", "华南", "小型", (byte) 1, "系统导入");

            when(customerMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(customer1, customer2, customer3));

            CustomerStatisticsVO result = customerOverviewService.getStatistics();

            assertNotNull(result);
            assertEquals(3, result.getTotalCustomers());
            assertEquals(2, result.getSourceDistribution().size());
            // 手动录入和系统导入各1个，null被过滤
            verify(customerMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("全部客户都是已看管-看管率100%")
        void getStatistics_allManaged() {
            Customer customer1 = buildCustomer(1L, "IT", "华东", "大型", (byte) 1, "手动录入");
            Customer customer2 = buildCustomer(2L, "金融", "华南", "中型", (byte) 1, "系统导入");

            when(customerMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(customer1, customer2));

            CustomerStatisticsVO result = customerOverviewService.getStatistics();

            assertNotNull(result);
            assertEquals(2, result.getTotalCustomers());
            assertEquals(2, result.getManagedCustomers());
            assertEquals(100.0, result.getManagementRate());
            verify(customerMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("全部客户都是未看管-看管率0%")
        void getStatistics_noneManaged() {
            Customer customer1 = buildCustomer(1L, "IT", "华东", "大型", (byte) 0, "手动录入");
            Customer customer2 = buildCustomer(2L, "金融", "华南", "中型", (byte) 0, "系统导入");

            when(customerMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(customer1, customer2));

            CustomerStatisticsVO result = customerOverviewService.getStatistics();

            assertNotNull(result);
            assertEquals(2, result.getTotalCustomers());
            assertEquals(0, result.getManagedCustomers());
            assertEquals(0.0, result.getManagementRate());
            verify(customerMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("行业分布按数量降序排序")
        void getStatistics_industryDistributionSorted() {
            Customer customer1 = buildCustomer(1L, "IT", "华东", "大型", (byte) 1, "手动录入");
            Customer customer2 = buildCustomer(2L, "IT", "华东", "中型", (byte) 0, "手动录入");
            Customer customer3 = buildCustomer(3L, "IT", "华南", "小型", (byte) 1, "系统导入");
            Customer customer4 = buildCustomer(4L, "金融", "华北", "大型", (byte) 1, "手动录入");
            Customer customer5 = buildCustomer(5L, "制造", "华东", "中型", (byte) 0, "系统导入");

            when(customerMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(customer1, customer2, customer3, customer4, customer5));

            CustomerStatisticsVO result = customerOverviewService.getStatistics();

            assertNotNull(result);
            assertEquals(3, result.getIndustryDistribution().size());
            // IT:3, 金融:1, 制造:1
            assertEquals("IT", result.getIndustryDistribution().get(0).getName());
            assertEquals(3, result.getIndustryDistribution().get(0).getCount());
            verify(customerMapper).selectList(any(LambdaQueryWrapper.class));
        }
    }

    // ==================== drillDown ====================

    @Nested
    @DisplayName("维度下钻分析测试")
    class DrillDownTest {

        @Test
        @DisplayName("行业维度下钻-带维度值过滤")
        void drillDown_industryWithDimensionValue() {
            Customer customer1 = buildCustomer(1L, "IT", "华东", "大型", (byte) 1, "手动录入");
            Customer customer2 = buildCustomer(2L, "IT", "华南", "中型", (byte) 0, "系统导入");
            Customer customer3 = buildCustomer(3L, "金融", "华东", "小型", (byte) 1, "手动录入");

            when(customerMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(customer1, customer2));

            DrillDownVO result = customerOverviewService.drillDown("industry", "IT", 1);

            assertNotNull(result);
            assertEquals("industry", result.getDimension());
            assertEquals("IT", result.getDimensionValue());
            assertEquals(2, result.getTotalCustomers());
            assertNotNull(result.getChildren());
            // 按规模细分：大型1个，中型1个
            assertEquals(2, result.getChildren().size());
            verify(customerMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("行业维度下钻-不带维度值，返回所有行业客户")
        void drillDown_industryWithoutDimensionValue() {
            Customer customer1 = buildCustomer(1L, "IT", "华东", "大型", (byte) 1, "手动录入");
            Customer customer2 = buildCustomer(2L, "金融", "华南", "中型", (byte) 0, "系统导入");

            when(customerMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(customer1, customer2));

            DrillDownVO result = customerOverviewService.drillDown("industry", null, 1);

            assertNotNull(result);
            assertEquals("industry", result.getDimension());
            assertNull(result.getDimensionValue());
            assertEquals(2, result.getTotalCustomers());
            verify(customerMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("区域维度下钻-带维度值过滤")
        void drillDown_regionWithDimensionValue() {
            Customer customer1 = buildCustomer(1L, "IT", "华东", "大型", (byte) 1, "手动录入");
            Customer customer2 = buildCustomer(2L, "金融", "华东", "中型", (byte) 0, "系统导入");
            Customer customer3 = buildCustomer(3L, "IT", "华南", "小型", (byte) 1, "手动录入");

            when(customerMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(customer1, customer2));

            DrillDownVO result = customerOverviewService.drillDown("region", "华东", 1);

            assertNotNull(result);
            assertEquals("region", result.getDimension());
            assertEquals("华东", result.getDimensionValue());
            assertEquals(2, result.getTotalCustomers());
            assertNotNull(result.getChildren());
            // 按行业细分：IT1个，金融1个
            assertEquals(2, result.getChildren().size());
            verify(customerMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("区域维度下钻-不带维度值，返回所有区域客户")
        void drillDown_regionWithoutDimensionValue() {
            Customer customer1 = buildCustomer(1L, "IT", "华东", "大型", (byte) 1, "手动录入");
            Customer customer2 = buildCustomer(2L, "金融", "华南", "中型", (byte) 0, "系统导入");

            when(customerMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(customer1, customer2));

            DrillDownVO result = customerOverviewService.drillDown("region", null, 1);

            assertNotNull(result);
            assertEquals("region", result.getDimension());
            assertNull(result.getDimensionValue());
            assertEquals(2, result.getTotalCustomers());
            verify(customerMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("其他维度下钻-不按任何条件过滤")
        void drillDown_otherDimension() {
            Customer customer1 = buildCustomer(1L, "IT", "华东", "大型", (byte) 1, "手动录入");
            Customer customer2 = buildCustomer(2L, "金融", "华南", "中型", (byte) 0, "系统导入");

            when(customerMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(customer1, customer2));

            DrillDownVO result = customerOverviewService.drillDown("other", null, 1);

            assertNotNull(result);
            assertEquals("other", result.getDimension());
            assertEquals(2, result.getTotalCustomers());
            // 其他维度不会构建子维度
            assertTrue(result.getChildren().isEmpty());
            verify(customerMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("行业维度下钻-客户规模包含null值，过滤null")
        void drillDown_industryWithNullScale() {
            Customer customer1 = buildCustomer(1L, "IT", "华东", "大型", (byte) 1, "手动录入");
            Customer customer2 = buildCustomer(2L, "IT", "华南", null, (byte) 0, "系统导入");
            Customer customer3 = buildCustomer(3L, "IT", "华北", "中型", (byte) 1, "手动录入");

            when(customerMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(customer1, customer2, customer3));

            DrillDownVO result = customerOverviewService.drillDown("industry", "IT", 1);

            assertNotNull(result);
            assertEquals(3, result.getTotalCustomers());
            // null规模被过滤，只有大型和中型
            assertEquals(2, result.getChildren().size());
            verify(customerMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("区域维度下钻-客户行业包含null值，过滤null")
        void drillDown_regionWithNullIndustry() {
            Customer customer1 = buildCustomer(1L, "IT", "华东", "大型", (byte) 1, "手动录入");
            Customer customer2 = buildCustomer(3L, null, "华东", "中型", (byte) 0, "系统导入");
            Customer customer3 = buildCustomer(3L, "金融", "华东", "小型", (byte) 1, "手动录入");

            when(customerMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(customer1, customer2, customer3));

            DrillDownVO result = customerOverviewService.drillDown("region", "华东", 1);

            assertNotNull(result);
            assertEquals(3, result.getTotalCustomers());
            // null行业被过滤，只有IT和金融
            assertEquals(2, result.getChildren().size());
            verify(customerMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("下钻分析-客户状态包含null，状态描述返回'未知'")
        void drillDown_withNullStatus() {
            Customer customer1 = buildCustomer(1L, "IT", "华东", "大型", null, "手动录入");

            when(customerMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.singletonList(customer1));

            DrillDownVO result = customerOverviewService.drillDown("industry", "IT", 1);

            assertNotNull(result);
            assertEquals(1, result.getCustomers().size());
            assertNull(result.getCustomers().get(0).getStatus());
            assertEquals("未知", result.getCustomers().get(0).getStatusDesc());
            verify(customerMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("下钻分析-客户状态为0，状态描述返回'未看管'")
        void drillDown_withStatusZero() {
            Customer customer1 = buildCustomer(1L, "IT", "华东", "大型", (byte) 0, "手动录入");

            when(customerMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.singletonList(customer1));

            DrillDownVO result = customerOverviewService.drillDown("industry", "IT", 1);

            assertNotNull(result);
            assertEquals(1, result.getCustomers().size());
            assertEquals(0, result.getCustomers().get(0).getStatus());
            assertEquals("未看管", result.getCustomers().get(0).getStatusDesc());
            verify(customerMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("下钻分析-客户状态为1，状态描述返回'已看管'")
        void drillDown_withStatusOne() {
            Customer customer1 = buildCustomer(1L, "IT", "华东", "大型", (byte) 1, "手动录入");

            when(customerMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.singletonList(customer1));

            DrillDownVO result = customerOverviewService.drillDown("industry", "IT", 1);

            assertNotNull(result);
            assertEquals(1, result.getCustomers().size());
            assertEquals(1, result.getCustomers().get(0).getStatus());
            assertEquals("已看管", result.getCustomers().get(0).getStatusDesc());
            verify(customerMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("下钻分析-客户状态为2，状态描述返回'已剔除'")
        void drillDown_withStatusTwo() {
            Customer customer1 = buildCustomer(1L, "IT", "华东", "大型", (byte) 2, "手动录入");

            when(customerMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.singletonList(customer1));

            DrillDownVO result = customerOverviewService.drillDown("industry", "IT", 1);

            assertNotNull(result);
            assertEquals(1, result.getCustomers().size());
            assertEquals(2, result.getCustomers().get(0).getStatus());
            assertEquals("已剔除", result.getCustomers().get(0).getStatusDesc());
            verify(customerMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("下钻分析-客户状态为其他值，状态描述返回'未知'")
        void drillDown_withOtherStatus() {
            Customer customer1 = buildCustomer(1L, "IT", "华东", "大型", (byte) 99, "手动录入");

            when(customerMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.singletonList(customer1));

            DrillDownVO result = customerOverviewService.drillDown("industry", "IT", 1);

            assertNotNull(result);
            assertEquals(1, result.getCustomers().size());
            assertEquals(99, result.getCustomers().get(0).getStatus());
            assertEquals("未知", result.getCustomers().get(0).getStatusDesc());
            verify(customerMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("下钻分析-超过50个客户，只返回前50个")
        void drillDown_limitToFiftyCustomers() {
            List<Customer> customers = new java.util.ArrayList<>();
            for (int i = 1; i <= 60; i++) {
                customers.add(buildCustomer((long) i, "IT", "华东", "大型", (byte) 1, "手动录入"));
            }

            when(customerMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(customers);

            DrillDownVO result = customerOverviewService.drillDown("industry", "IT", 1);

            assertNotNull(result);
            assertEquals(60, result.getTotalCustomers());
            assertEquals(50, result.getCustomers().size());
            verify(customerMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("下钻分析-少于50个客户，全部返回")
        void drillDown_lessThanFiftyCustomers() {
            List<Customer> customers = new java.util.ArrayList<>();
            for (int i = 1; i <= 30; i++) {
                customers.add(buildCustomer((long) i, "IT", "华东", "大型", (byte) 1, "手动录入"));
            }

            when(customerMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(customers);

            DrillDownVO result = customerOverviewService.drillDown("industry", "IT", 1);

            assertNotNull(result);
            assertEquals(30, result.getTotalCustomers());
            assertEquals(30, result.getCustomers().size());
            verify(customerMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("异常测试-Mapper查询返回null")
        void drillDown_mapperReturnsNull() {
            when(customerMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(null);

            DrillDownVO result = customerOverviewService.drillDown("industry", "IT", 1);

            assertNotNull(result);
            assertEquals(0, result.getTotalCustomers());
            assertTrue(result.getChildren().isEmpty());
            assertTrue(result.getCustomers().isEmpty());
            verify(customerMapper).selectList(any(LambdaQueryWrapper.class));
        }
    }

    // ==================== Helper Methods ====================

    private Customer buildCustomer(Long id, String industry, String region, String scale, Byte status, String source) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setCustomerCode("CUS" + String.format("%06d", id));
        customer.setCustomerName("客户" + id);
        customer.setIndustry(industry);
        customer.setRegion(region);
        customer.setScale(scale);
        customer.setStatus(status);
        customer.setManagerName("经理" + id);
        customer.setJoinTime(LocalDateTime.now());
        customer.setSource(source);
        return customer;
    }
}
