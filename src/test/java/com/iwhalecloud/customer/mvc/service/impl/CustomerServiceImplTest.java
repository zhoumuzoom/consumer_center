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
import com.iwhalecloud.customer.mvc.service.OperationLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 客户服务实现测试 - 全分支覆盖
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private OperationLogService operationLogService;

    private Customer buildCustomer(Long id, Byte status) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setCustomerCode("CUS20260703120000ABCD");
        customer.setCustomerName("测试客户");
        customer.setCustomerNameEn("Test Customer");
        customer.setIndustry("IT");
        customer.setRegion("华东");
        customer.setScale("大型");
        customer.setStatus(status);
        customer.setManagerId(1L);
        customer.setManagerName("张三");
        customer.setJoinTime(LocalDateTime.of(2026, 1, 1, 0, 0));
        customer.setSource("手动录入");
        customer.setContactPerson("李四");
        customer.setContactPhone("13800138000");
        customer.setContactEmail("test@example.com");
        customer.setAddress("上海市浦东新区");
        customer.setRemark("测试备注");
        customer.setCreateTime(LocalDateTime.of(2026, 1, 1, 0, 0));
        customer.setUpdateTime(LocalDateTime.of(2026, 1, 2, 0, 0));
        return customer;
    }

    // ==================== pageList 分支测试 ====================

    @Nested
    @DisplayName("分页查询测试")
    class PageListTest {

        @Test
        @DisplayName("分页查询-无查询条件（所有if分支均不进入）")
        void pageList_noFilters() {
            CustomerQueryDTO queryDTO = new CustomerQueryDTO();
            queryDTO.setPage(1);
            queryDTO.setSize(10);
            // keyword, industry, region, scale, status, source 全部为空

            Page<Customer> page = new Page<>(1, 10);
            page.setRecords(java.util.List.of(buildCustomer(1L, (byte) 0)));
            page.setTotal(1);

            when(customerMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

            PageResult<CustomerVO> result = customerService.pageList(queryDTO);

            assertNotNull(result);
            assertEquals(1L, result.getTotal());
            assertEquals(1, result.getRecords().size());
            verify(customerMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("分页查询-仅keyword条件（进入keyword分支）")
        void pageList_withKeyword() {
            CustomerQueryDTO queryDTO = new CustomerQueryDTO();
            queryDTO.setPage(1);
            queryDTO.setSize(10);
            queryDTO.setKeyword("测试");

            Page<Customer> page = new Page<>(1, 10);
            page.setRecords(java.util.List.of(buildCustomer(1L, (byte) 0)));
            page.setTotal(1);

            when(customerMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

            PageResult<CustomerVO> result = customerService.pageList(queryDTO);

            assertNotNull(result);
            assertEquals(1L, result.getTotal());
        }

        @Test
        @DisplayName("分页查询-仅industry条件（进入industry分支）")
        void pageList_withIndustry() {
            CustomerQueryDTO queryDTO = new CustomerQueryDTO();
            queryDTO.setPage(1);
            queryDTO.setSize(10);
            queryDTO.setIndustry("IT");

            Page<Customer> page = new Page<>(1, 10);
            page.setRecords(java.util.List.of());
            page.setTotal(0);

            when(customerMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

            PageResult<CustomerVO> result = customerService.pageList(queryDTO);

            assertNotNull(result);
            assertEquals(0L, result.getTotal());
        }

        @Test
        @DisplayName("分页查询-仅region条件（进入region分支）")
        void pageList_withRegion() {
            CustomerQueryDTO queryDTO = new CustomerQueryDTO();
            queryDTO.setPage(1);
            queryDTO.setSize(10);
            queryDTO.setRegion("华东");

            Page<Customer> page = new Page<>(1, 10);
            page.setRecords(java.util.List.of());
            page.setTotal(0);

            when(customerMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

            PageResult<CustomerVO> result = customerService.pageList(queryDTO);

            assertNotNull(result);
        }

        @Test
        @DisplayName("分页查询-仅scale条件（进入scale分支）")
        void pageList_withScale() {
            CustomerQueryDTO queryDTO = new CustomerQueryDTO();
            queryDTO.setPage(1);
            queryDTO.setSize(10);
            queryDTO.setScale("大型");

            Page<Customer> page = new Page<>(1, 10);
            page.setRecords(java.util.List.of());
            page.setTotal(0);

            when(customerMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

            PageResult<CustomerVO> result = customerService.pageList(queryDTO);

            assertNotNull(result);
        }

        @Test
        @DisplayName("分页查询-仅status条件（进入status分支）")
        void pageList_withStatus() {
            CustomerQueryDTO queryDTO = new CustomerQueryDTO();
            queryDTO.setPage(1);
            queryDTO.setSize(10);
            queryDTO.setStatus(1);

            Page<Customer> page = new Page<>(1, 10);
            page.setRecords(java.util.List.of(buildCustomer(1L, (byte) 1)));
            page.setTotal(1);

            when(customerMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

            PageResult<CustomerVO> result = customerService.pageList(queryDTO);

            assertNotNull(result);
            assertEquals(1L, result.getTotal());
        }

        @Test
        @DisplayName("分页查询-仅source条件（进入source分支）")
        void pageList_withSource() {
            CustomerQueryDTO queryDTO = new CustomerQueryDTO();
            queryDTO.setPage(1);
            queryDTO.setSize(10);
            queryDTO.setSource("手动录入");

            Page<Customer> page = new Page<>(1, 10);
            page.setRecords(java.util.List.of());
            page.setTotal(0);

            when(customerMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

            PageResult<CustomerVO> result = customerService.pageList(queryDTO);

            assertNotNull(result);
        }

        @Test
        @DisplayName("分页查询-所有条件同时设置（进入所有分支）")
        void pageList_allFilters() {
            CustomerQueryDTO queryDTO = new CustomerQueryDTO();
            queryDTO.setPage(1);
            queryDTO.setSize(10);
            queryDTO.setKeyword("测试");
            queryDTO.setIndustry("IT");
            queryDTO.setRegion("华东");
            queryDTO.setScale("大型");
            queryDTO.setStatus(0);
            queryDTO.setSource("手动录入");

            Page<Customer> page = new Page<>(1, 10);
            page.setRecords(java.util.List.of(buildCustomer(1L, (byte) 0)));
            page.setTotal(1);

            when(customerMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

            PageResult<CustomerVO> result = customerService.pageList(queryDTO);

            assertNotNull(result);
            assertEquals(1L, result.getTotal());
            assertEquals(1, result.getRecords().size());
        }
    }

    // ==================== getDetail 分支测试 ====================

    @Nested
    @DisplayName("客户详情查询测试")
    class GetDetailTest {

        @Test
        @DisplayName("客户详情查询-客户存在，正常返回")
        void getDetail_success() {
            Customer customer = buildCustomer(1L, (byte) 0);
            when(customerMapper.selectById(1L)).thenReturn(customer);

            CustomerDetailVO result = customerService.getDetail(1L);

            assertNotNull(result);
            assertEquals("测试客户", result.getCustomerName());
            assertEquals("test@example.com", result.getContactEmail());
            assertEquals("上海市浦东新区", result.getAddress());
            assertEquals("测试备注", result.getRemark());
            assertEquals(3, result.getBranchCount());
            assertEquals(2, result.getDecisionMakerCount());
            assertEquals(5, result.getInteractionCount());
        }

        @Test
        @DisplayName("异常测试-客户详情查询时客户不存在，抛出RuntimeException")
        void getDetail_customerNotFound() {
            when(customerMapper.selectById(999L)).thenReturn(null);

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> customerService.getDetail(999L));

            assertEquals("客户不存在", exception.getMessage());
        }
    }

    // ==================== create 测试 ====================

    @Nested
    @DisplayName("新增客户测试")
    class CreateTest {

        @Test
        @DisplayName("新增客户-正常创建并记录操作日志")
        void create_success() {
            CustomerCreateDTO createDTO = new CustomerCreateDTO();
            createDTO.setCustomerName("新客户");
            createDTO.setIndustry("IT");
            createDTO.setRegion("华东");
            createDTO.setScale("大型");
            createDTO.setContactPerson("王五");
            createDTO.setContactPhone("13900139000");
            createDTO.setContactEmail("new@example.com");
            createDTO.setAddress("北京市朝阳区");
            createDTO.setRemark("新客户备注");

            when(customerMapper.insert(any(Customer.class))).thenAnswer(invocation -> {
                Customer customer = invocation.getArgument(0);
                customer.setId(100L);
                return 1;
            });
            doNothing().when(operationLogService).log(anyLong(), anyString(), anyString(), anyString());

            CustomerVO result = customerService.create(createDTO);

            assertNotNull(result);
            assertEquals(100L, result.getId());
            assertNotNull(result.getCustomerCode());
            assertEquals("新客户", result.getCustomerName());

            // 验证操作日志被调用
            verify(operationLogService).log(eq(100L), eq("customer"), eq("CREATE"), anyString());
        }
    }

    // ==================== update 分支测试 ====================

    @Nested
    @DisplayName("修改客户测试")
    class UpdateTest {

        @Test
        @DisplayName("修改客户-客户存在，正常更新并记录操作日志")
        void update_success() {
            Customer existingCustomer = buildCustomer(1L, (byte) 0);
            when(customerMapper.selectById(1L)).thenReturn(existingCustomer);
            when(customerMapper.updateById(any(Customer.class))).thenReturn(1);
            doNothing().when(operationLogService).log(anyLong(), anyString(), anyString(), anyString(), anyString(), anyString());

            CustomerUpdateDTO updateDTO = new CustomerUpdateDTO();
            updateDTO.setId(1L);
            updateDTO.setCustomerName("修改后的客户");
            updateDTO.setIndustry("金融");

            CustomerVO result = customerService.update(updateDTO);

            assertNotNull(result);
            assertEquals("修改后的客户", result.getCustomerName());
            assertEquals("金融", result.getIndustry());

            // 验证操作日志被调用（带前后数据）
            verify(operationLogService).log(eq(1L), eq("customer"), eq("UPDATE"),
                    anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("异常测试-修改客户时客户不存在，抛出RuntimeException")
        void update_customerNotFound() {
            when(customerMapper.selectById(999L)).thenReturn(null);

            CustomerUpdateDTO updateDTO = new CustomerUpdateDTO();
            updateDTO.setId(999L);
            updateDTO.setCustomerName("修改后的客户");

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> customerService.update(updateDTO));

            assertEquals("客户不存在", exception.getMessage());
            // 验证未执行更新和日志操作
            verify(customerMapper, never()).updateById(any(Customer.class));
            verify(operationLogService, never()).log(anyLong(), anyString(), anyString(), anyString(), anyString(), anyString());
        }
    }

    // ==================== delete 分支测试 ====================

    @Nested
    @DisplayName("删除客户测试")
    class DeleteTest {

        @Test
        @DisplayName("删除客户-客户存在，正常删除并记录操作日志")
        void delete_success() {
            Customer customer = buildCustomer(1L, (byte) 0);
            when(customerMapper.selectById(1L)).thenReturn(customer);
            when(customerMapper.deleteById(1L)).thenReturn(1);
            doNothing().when(operationLogService).log(anyLong(), anyString(), anyString(), anyString());

            customerService.delete(1L);

            verify(customerMapper).deleteById(1L);
            verify(operationLogService).log(eq(1L), eq("customer"), eq("DELETE"), anyString());
        }

        @Test
        @DisplayName("异常测试-删除客户时客户不存在，抛出RuntimeException")
        void delete_customerNotFound() {
            when(customerMapper.selectById(999L)).thenReturn(null);

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> customerService.delete(999L));

            assertEquals("客户不存在", exception.getMessage());
            // 验证未执行删除和日志操作
            verify(customerMapper, never()).deleteById(anyLong());
            verify(operationLogService, never()).log(anyLong(), anyString(), anyString(), anyString());
        }
    }

    // ==================== updateStatus 分支测试 ====================

    @Nested
    @DisplayName("客户状态变更测试")
    class UpdateStatusTest {

        @Test
        @DisplayName("状态变更-状态不为空且原因不为空（进入status非空分支+reason非空分支）")
        void updateStatus_withStatusAndReason() {
            Customer customer = buildCustomer(1L, (byte) 0);
            when(customerMapper.selectById(1L)).thenReturn(customer);
            when(customerMapper.updateById(any(Customer.class))).thenReturn(1);
            doNothing().when(operationLogService).log(anyLong(), anyString(), anyString(), anyString());

            CustomerStatusUpdateDTO statusDTO = new CustomerStatusUpdateDTO();
            statusDTO.setStatus(1);
            statusDTO.setReason("需要重点跟进");

            CustomerVO result = customerService.updateStatus(1L, statusDTO);

            assertNotNull(result);
            // 验证操作日志包含原因
            ArgumentCaptor<String> descCaptor = ArgumentCaptor.forClass(String.class);
            verify(operationLogService).log(eq(1L), eq("customer"), eq("STATUS_CHANGE"), descCaptor.capture());
            String logDesc = descCaptor.getValue();
            assert logDesc.contains("原因: 需要重点跟进");
            assert logDesc.contains("未看管");
            assert logDesc.contains("已看管");
        }

        @Test
        @DisplayName("状态变更-状态不为空但原因为空（进入status非空分支，不进入reason非空分支）")
        void updateStatus_withStatusNoReason() {
            Customer customer = buildCustomer(1L, (byte) 0);
            when(customerMapper.selectById(1L)).thenReturn(customer);
            when(customerMapper.updateById(any(Customer.class))).thenReturn(1);
            doNothing().when(operationLogService).log(anyLong(), anyString(), anyString(), anyString());

            CustomerStatusUpdateDTO statusDTO = new CustomerStatusUpdateDTO();
            statusDTO.setStatus(2);
            statusDTO.setReason(null);

            CustomerVO result = customerService.updateStatus(1L, statusDTO);

            assertNotNull(result);
            // 验证操作日志不包含原因
            ArgumentCaptor<String> descCaptor = ArgumentCaptor.forClass(String.class);
            verify(operationLogService).log(eq(1L), eq("customer"), eq("STATUS_CHANGE"), descCaptor.capture());
            String logDesc = descCaptor.getValue();
            assert !logDesc.contains("原因:");
            assert logDesc.contains("已剔除");
        }

        @Test
        @DisplayName("状态变更-状态为空（进入status为空分支）")
        void updateStatus_withNullStatus() {
            Customer customer = buildCustomer(1L, (byte) 0);
            when(customerMapper.selectById(1L)).thenReturn(customer);
            when(customerMapper.updateById(any(Customer.class))).thenReturn(1);
            doNothing().when(operationLogService).log(anyLong(), anyString(), anyString(), anyString());

            CustomerStatusUpdateDTO statusDTO = new CustomerStatusUpdateDTO();
            statusDTO.setStatus(null);
            statusDTO.setReason(null);

            CustomerVO result = customerService.updateStatus(1L, statusDTO);

            assertNotNull(result);
            // 验证日志中的状态描述为"未知"
            ArgumentCaptor<String> descCaptor = ArgumentCaptor.forClass(String.class);
            verify(operationLogService).log(eq(1L), eq("customer"), eq("STATUS_CHANGE"), descCaptor.capture());
            String logDesc = descCaptor.getValue();
            assert logDesc.contains("未知");
        }

        @Test
        @DisplayName("异常测试-状态变更时客户不存在，抛出RuntimeException")
        void updateStatus_customerNotFound() {
            when(customerMapper.selectById(999L)).thenReturn(null);

            CustomerStatusUpdateDTO statusDTO = new CustomerStatusUpdateDTO();
            statusDTO.setStatus(1);

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> customerService.updateStatus(999L, statusDTO));

            assertEquals("客户不存在", exception.getMessage());
            verify(customerMapper, never()).updateById(any(Customer.class));
        }
    }

    // ==================== getStatusDesc 分支测试（通过getDetail间接测试） ====================

    @Nested
    @DisplayName("状态描述转换测试（getStatusDesc私有方法）")
    class GetStatusDescTest {

        @Test
        @DisplayName("状态描述-status=0，返回'未看管'")
        void statusDesc_0_未看管() {
            Customer customer = buildCustomer(1L, (byte) 0);
            when(customerMapper.selectById(1L)).thenReturn(customer);

            CustomerDetailVO result = customerService.getDetail(1L);

            assertEquals("未看管", result.getStatusDesc());
        }

        @Test
        @DisplayName("状态描述-status=1，返回'已看管'")
        void statusDesc_1_已看管() {
            Customer customer = buildCustomer(2L, (byte) 1);
            when(customerMapper.selectById(2L)).thenReturn(customer);

            CustomerDetailVO result = customerService.getDetail(2L);

            assertEquals("已看管", result.getStatusDesc());
        }

        @Test
        @DisplayName("状态描述-status=2，返回'已剔除'")
        void statusDesc_2_已剔除() {
            Customer customer = buildCustomer(3L, (byte) 2);
            when(customerMapper.selectById(3L)).thenReturn(customer);

            CustomerDetailVO result = customerService.getDetail(3L);

            assertEquals("已剔除", result.getStatusDesc());
        }

        @Test
        @DisplayName("状态描述-status=null，返回'未知'")
        void statusDesc_null_未知() {
            Customer customer = buildCustomer(4L, null);
            when(customerMapper.selectById(4L)).thenReturn(customer);

            CustomerDetailVO result = customerService.getDetail(4L);

            assertEquals("未知", result.getStatusDesc());
        }

        @Test
        @DisplayName("状态描述-status=99（其他值），返回'未知'")
        void statusDesc_99_未知() {
            Customer customer = buildCustomer(5L, (byte) 99);
            when(customerMapper.selectById(5L)).thenReturn(customer);

            CustomerDetailVO result = customerService.getDetail(5L);

            assertEquals("未知", result.getStatusDesc());
        }
    }
}
