package com.iwhalecloud.customer.mvc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iwhalecloud.customer.common.result.PageResult;
import com.iwhalecloud.customer.mvc.mapper.CustomerMapper;
import com.iwhalecloud.customer.mvc.mapper.DecisionChainMapper;
import com.iwhalecloud.customer.mvc.mapper.DecisionMakerMapper;
import com.iwhalecloud.customer.mvc.pojo.dto.decision.*;
import com.iwhalecloud.customer.mvc.pojo.entity.Customer;
import com.iwhalecloud.customer.mvc.pojo.entity.DecisionChain;
import com.iwhalecloud.customer.mvc.pojo.entity.DecisionMaker;
import com.iwhalecloud.customer.mvc.pojo.vo.decision.DecisionChainVO;
import com.iwhalecloud.customer.mvc.pojo.vo.decision.DecisionMakerVO;
import com.iwhalecloud.customer.mvc.service.OperationLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 决策关键人服务实现测试 - 全分支覆盖
 */
@ExtendWith(MockitoExtension.class)
class DecisionMakerServiceImplTest {

    @InjectMocks
    private DecisionMakerServiceImpl decisionMakerService;

    @Mock
    private DecisionMakerMapper decisionMakerMapper;

    @Mock
    private DecisionChainMapper decisionChainMapper;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private OperationLogService operationLogService;

    private DecisionChain buildDecisionChain(Long id) {
        DecisionChain chain = new DecisionChain();
        chain.setId(id);
        chain.setCustomerId(10L);
        chain.setDecisionMakerId(100L);
        chain.setProjectName("智慧城市项目");
        chain.setDecisionStage("方案审批");
        chain.setInfluenceLevel("高");
        chain.setSortOrder(1);
        chain.setRemark("测试备注");
        chain.setCreateTime(LocalDateTime.of(2026, 1, 1, 0, 0));
        chain.setUpdateTime(LocalDateTime.of(2026, 1, 2, 0, 0));
        return chain;
    }

    private DecisionMaker buildDecisionMaker(Long id) {
        DecisionMaker maker = new DecisionMaker();
        maker.setId(id);
        maker.setName("张三");
        maker.setPosition("CTO");
        maker.setDepartment("技术部");
        maker.setPhone("13800138000");
        maker.setEmail("zhangsan@example.com");
        maker.setRole("方案审批");
        maker.setCompany("测试公司");
        maker.setCreateTime(LocalDateTime.of(2026, 1, 1, 0, 0));
        maker.setUpdateTime(LocalDateTime.of(2026, 1, 2, 0, 0));
        return maker;
    }

    private Customer buildCustomer(Long id) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setCustomerName("测试客户");
        return customer;
    }

    private void mockConvertToChainVO() {
        when(customerMapper.selectById(anyLong())).thenReturn(buildCustomer(10L));
        when(decisionMakerMapper.selectById(anyLong())).thenReturn(buildDecisionMaker(100L));
    }

    // ==================== pageDecisionChains 分支测试 ====================

    @Nested
    @DisplayName("决策链分页查询测试")
    class PageDecisionChainsTest {

        @Test
        @DisplayName("分页查询-无查询条件（所有if分支均不进入）")
        void pageDecisionChains_noFilters() {
            DecisionChainQueryDTO queryDTO = new DecisionChainQueryDTO();
            queryDTO.setPage(1);
            queryDTO.setSize(10);

            Page<DecisionChain> page = new Page<>(1, 10);
            page.setRecords(List.of(buildDecisionChain(1L)));
            page.setTotal(1);

            when(decisionChainMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
            mockConvertToChainVO();

            PageResult<DecisionChainVO> result = decisionMakerService.pageDecisionChains(queryDTO);

            assertNotNull(result);
            assertEquals(1L, result.getTotal());
            assertEquals(1, result.getRecords().size());
            verify(decisionChainMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("分页查询-仅projectName条件（进入projectName分支）")
        void pageDecisionChains_withProjectName() {
            DecisionChainQueryDTO queryDTO = new DecisionChainQueryDTO();
            queryDTO.setPage(1);
            queryDTO.setSize(10);
            queryDTO.setProjectName("智慧城市");

            Page<DecisionChain> page = new Page<>(1, 10);
            page.setRecords(List.of(buildDecisionChain(1L)));
            page.setTotal(1);

            when(decisionChainMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
            mockConvertToChainVO();

            PageResult<DecisionChainVO> result = decisionMakerService.pageDecisionChains(queryDTO);

            assertNotNull(result);
            assertEquals(1, result.getRecords().size());
        }

        @Test
        @DisplayName("分页查询-仅customerName条件（进入customerName过滤分支）")
        void pageDecisionChains_withCustomerName() {
            DecisionChainQueryDTO queryDTO = new DecisionChainQueryDTO();
            queryDTO.setPage(1);
            queryDTO.setSize(10);
            queryDTO.setCustomerName("测试客户");

            Page<DecisionChain> page = new Page<>(1, 10);
            page.setRecords(List.of(buildDecisionChain(1L)));
            page.setTotal(1);

            when(decisionChainMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
            mockConvertToChainVO();

            PageResult<DecisionChainVO> result = decisionMakerService.pageDecisionChains(queryDTO);

            assertNotNull(result);
            assertEquals(1, result.getRecords().size());
            assertEquals("测试客户", result.getRecords().get(0).getCustomerName());
        }

        @Test
        @DisplayName("分页查询-customerName不匹配（customerName过滤掉结果）")
        void pageDecisionChains_customerNameNotMatch() {
            DecisionChainQueryDTO queryDTO = new DecisionChainQueryDTO();
            queryDTO.setPage(1);
            queryDTO.setSize(10);
            queryDTO.setCustomerName("不存在的客户");

            Page<DecisionChain> page = new Page<>(1, 10);
            page.setRecords(List.of(buildDecisionChain(1L)));
            page.setTotal(1);

            when(decisionChainMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
            mockConvertToChainVO();

            PageResult<DecisionChainVO> result = decisionMakerService.pageDecisionChains(queryDTO);

            assertNotNull(result);
            assertEquals(0, result.getRecords().size());
        }

        @Test
        @DisplayName("分页查询-仅decisionMakerName条件（进入decisionMakerName过滤分支）")
        void pageDecisionChains_withDecisionMakerName() {
            DecisionChainQueryDTO queryDTO = new DecisionChainQueryDTO();
            queryDTO.setPage(1);
            queryDTO.setSize(10);
            queryDTO.setDecisionMakerName("张三");

            Page<DecisionChain> page = new Page<>(1, 10);
            page.setRecords(List.of(buildDecisionChain(1L)));
            page.setTotal(1);

            when(decisionChainMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
            mockConvertToChainVO();

            PageResult<DecisionChainVO> result = decisionMakerService.pageDecisionChains(queryDTO);

            assertNotNull(result);
            assertEquals(1, result.getRecords().size());
        }

        @Test
        @DisplayName("分页查询-decisionMakerName不匹配（过滤掉结果）")
        void pageDecisionChains_decisionMakerNameNotMatch() {
            DecisionChainQueryDTO queryDTO = new DecisionChainQueryDTO();
            queryDTO.setPage(1);
            queryDTO.setSize(10);
            queryDTO.setDecisionMakerName("不存在的人");

            Page<DecisionChain> page = new Page<>(1, 10);
            page.setRecords(List.of(buildDecisionChain(1L)));
            page.setTotal(1);

            when(decisionChainMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
            mockConvertToChainVO();

            PageResult<DecisionChainVO> result = decisionMakerService.pageDecisionChains(queryDTO);

            assertNotNull(result);
            assertEquals(0, result.getRecords().size());
        }

        @Test
        @DisplayName("分页查询-仅position条件（进入position过滤分支）")
        void pageDecisionChains_withPosition() {
            DecisionChainQueryDTO queryDTO = new DecisionChainQueryDTO();
            queryDTO.setPage(1);
            queryDTO.setSize(10);
            queryDTO.setPosition("CTO");

            Page<DecisionChain> page = new Page<>(1, 10);
            page.setRecords(List.of(buildDecisionChain(1L)));
            page.setTotal(1);

            when(decisionChainMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
            mockConvertToChainVO();

            PageResult<DecisionChainVO> result = decisionMakerService.pageDecisionChains(queryDTO);

            assertNotNull(result);
            assertEquals(1, result.getRecords().size());
        }

        @Test
        @DisplayName("分页查询-position不匹配（过滤掉结果）")
        void pageDecisionChains_positionNotMatch() {
            DecisionChainQueryDTO queryDTO = new DecisionChainQueryDTO();
            queryDTO.setPage(1);
            queryDTO.setSize(10);
            queryDTO.setPosition("CEO");

            Page<DecisionChain> page = new Page<>(1, 10);
            page.setRecords(List.of(buildDecisionChain(1L)));
            page.setTotal(1);

            when(decisionChainMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
            mockConvertToChainVO();

            PageResult<DecisionChainVO> result = decisionMakerService.pageDecisionChains(queryDTO);

            assertNotNull(result);
            assertEquals(0, result.getRecords().size());
        }

        @Test
        @DisplayName("分页查询-所有条件都满足")
        void pageDecisionChains_allFilters() {
            DecisionChainQueryDTO queryDTO = new DecisionChainQueryDTO();
            queryDTO.setPage(1);
            queryDTO.setSize(10);
            queryDTO.setProjectName("智慧城市");
            queryDTO.setCustomerName("测试客户");
            queryDTO.setDecisionMakerName("张三");
            queryDTO.setPosition("CTO");

            Page<DecisionChain> page = new Page<>(1, 10);
            page.setRecords(List.of(buildDecisionChain(1L)));
            page.setTotal(1);

            when(decisionChainMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
            mockConvertToChainVO();

            PageResult<DecisionChainVO> result = decisionMakerService.pageDecisionChains(queryDTO);

            assertNotNull(result);
            assertEquals(1, result.getRecords().size());
        }

        @Test
        @DisplayName("分页查询-convertToChainVO中customer为null")
        void pageDecisionChains_customerNull() {
            DecisionChainQueryDTO queryDTO = new DecisionChainQueryDTO();
            queryDTO.setPage(1);
            queryDTO.setSize(10);

            Page<DecisionChain> page = new Page<>(1, 10);
            page.setRecords(List.of(buildDecisionChain(1L)));
            page.setTotal(1);

            when(decisionChainMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
            when(customerMapper.selectById(anyLong())).thenReturn(null);
            when(decisionMakerMapper.selectById(anyLong())).thenReturn(buildDecisionMaker(100L));

            PageResult<DecisionChainVO> result = decisionMakerService.pageDecisionChains(queryDTO);

            assertNotNull(result);
            assertEquals(1, result.getRecords().size());
            assertNull(result.getRecords().get(0).getCustomerName());
        }

        @Test
        @DisplayName("分页查询-convertToChainVO中maker为null")
        void pageDecisionChains_makerNull() {
            DecisionChainQueryDTO queryDTO = new DecisionChainQueryDTO();
            queryDTO.setPage(1);
            queryDTO.setSize(10);

            Page<DecisionChain> page = new Page<>(1, 10);
            page.setRecords(List.of(buildDecisionChain(1L)));
            page.setTotal(1);

            when(decisionChainMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
            when(customerMapper.selectById(anyLong())).thenReturn(buildCustomer(10L));
            when(decisionMakerMapper.selectById(anyLong())).thenReturn(null);

            PageResult<DecisionChainVO> result = decisionMakerService.pageDecisionChains(queryDTO);

            assertNotNull(result);
            assertEquals(1, result.getRecords().size());
            assertNull(result.getRecords().get(0).getDecisionMakerName());
            assertNull(result.getRecords().get(0).getDecisionMakerPosition());
        }
    }

    // ==================== getDecisionChainDetail ====================

    @Nested
    @DisplayName("决策链详情查询测试")
    class GetDecisionChainDetailTest {

        @Test
        @DisplayName("决策链存在-正常返回详情")
        void getDecisionChainDetail_success() {
            when(decisionChainMapper.selectById(1L)).thenReturn(buildDecisionChain(1L));
            mockConvertToChainVO();

            DecisionChainVO vo = decisionMakerService.getDecisionChainDetail(1L);

            assertNotNull(vo);
            assertEquals(1L, vo.getId());
            assertEquals("智慧城市项目", vo.getProjectName());
            verify(decisionChainMapper).selectById(1L);
        }

        @Test
        @DisplayName("异常测试-决策链不存在-抛出RuntimeException")
        void getDecisionChainDetail_notFound() {
            when(decisionChainMapper.selectById(999L)).thenReturn(null);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> decisionMakerService.getDecisionChainDetail(999L));

            assertEquals("决策链不存在", ex.getMessage());
        }
    }

    // ==================== createDecisionMaker ====================

    @Nested
    @DisplayName("新增决策人测试")
    class CreateDecisionMakerTest {

        @Test
        @DisplayName("新增决策人-正常创建")
        void createDecisionMaker_success() {
            DecisionMakerCreateDTO createDTO = new DecisionMakerCreateDTO();
            createDTO.setName("张三");
            createDTO.setPosition("CTO");
            createDTO.setDepartment("技术部");
            createDTO.setPhone("13800138000");
            createDTO.setEmail("zhangsan@example.com");
            createDTO.setRole("方案审批");
            createDTO.setCompany("测试公司");

            when(decisionMakerMapper.insert(any(DecisionMaker.class))).thenReturn(1);

            DecisionMakerVO vo = decisionMakerService.createDecisionMaker(createDTO);

            assertNotNull(vo);
            assertEquals("张三", vo.getName());
            assertEquals("CTO", vo.getPosition());
            verify(decisionMakerMapper).insert(any(DecisionMaker.class));
            verify(operationLogService).log(any(), eq("decision_maker"), eq("CREATE"), anyString());
        }
    }

    // ==================== updateDecisionMaker ====================

    @Nested
    @DisplayName("修改决策人测试")
    class UpdateDecisionMakerTest {

        @Test
        @DisplayName("决策人存在-正常更新")
        void updateDecisionMaker_success() {
            DecisionMakerUpdateDTO updateDTO = new DecisionMakerUpdateDTO();
            updateDTO.setId(100L);
            updateDTO.setName("李四");
            updateDTO.setPosition("CEO");

            when(decisionMakerMapper.selectById(100L)).thenReturn(buildDecisionMaker(100L));
            when(decisionMakerMapper.updateById(any(DecisionMaker.class))).thenReturn(1);

            DecisionMakerVO vo = decisionMakerService.updateDecisionMaker(updateDTO);

            assertNotNull(vo);
            verify(decisionMakerMapper).selectById(100L);
            verify(decisionMakerMapper).updateById(any(DecisionMaker.class));
            verify(operationLogService).log(eq(100L), eq("decision_maker"), eq("UPDATE"), anyString());
        }

        @Test
        @DisplayName("异常测试-决策人不存在-抛出RuntimeException")
        void updateDecisionMaker_notFound() {
            DecisionMakerUpdateDTO updateDTO = new DecisionMakerUpdateDTO();
            updateDTO.setId(999L);
            updateDTO.setName("李四");

            when(decisionMakerMapper.selectById(999L)).thenReturn(null);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> decisionMakerService.updateDecisionMaker(updateDTO));

            assertEquals("决策人不存在", ex.getMessage());
            verify(decisionMakerMapper, never()).updateById(any(DecisionMaker.class));
        }
    }

    // ==================== deleteDecisionMaker ====================

    @Nested
    @DisplayName("删除决策人测试")
    class DeleteDecisionMakerTest {

        @Test
        @DisplayName("删除决策人-正常删除")
        void deleteDecisionMaker_success() {
            when(decisionMakerMapper.deleteById(100L)).thenReturn(1);

            decisionMakerService.deleteDecisionMaker(100L);

            verify(decisionMakerMapper).deleteById(100L);
            verify(operationLogService).log(eq(100L), eq("decision_maker"), eq("DELETE"), eq("删除决策人"));
        }
    }

    // ==================== createDecisionChain ====================

    @Nested
    @DisplayName("建立决策链关系测试")
    class CreateDecisionChainTest {

        @Test
        @DisplayName("建立决策链关系-正常创建")
        void createDecisionChain_success() {
            DecisionChainCreateDTO createDTO = new DecisionChainCreateDTO();
            createDTO.setCustomerId(10L);
            createDTO.setDecisionMakerId(100L);
            createDTO.setProjectName("智慧城市项目");
            createDTO.setDecisionStage("方案审批");
            createDTO.setInfluenceLevel("高");
            createDTO.setSortOrder(1);
            createDTO.setRemark("测试备注");

            when(decisionChainMapper.insert(any(DecisionChain.class))).thenReturn(1);
            mockConvertToChainVO();

            DecisionChainVO vo = decisionMakerService.createDecisionChain(createDTO);

            assertNotNull(vo);
            assertEquals("智慧城市项目", vo.getProjectName());
            verify(decisionChainMapper).insert(any(DecisionChain.class));
            verify(operationLogService).log(any(), eq("decision_chain"), eq("CREATE"), eq("建立决策链关系"));
        }
    }

    // ==================== updateDecisionChain ====================

    @Nested
    @DisplayName("更新决策链关系测试")
    class UpdateDecisionChainTest {

        @Test
        @DisplayName("决策链存在-正常更新")
        void updateDecisionChain_success() {
            DecisionChainUpdateDTO updateDTO = new DecisionChainUpdateDTO();
            updateDTO.setId(1L);
            updateDTO.setProjectName("更新后的项目");
            updateDTO.setDecisionStage("项目实施");

            when(decisionChainMapper.selectById(1L)).thenReturn(buildDecisionChain(1L));
            when(decisionChainMapper.updateById(any(DecisionChain.class))).thenReturn(1);
            mockConvertToChainVO();

            DecisionChainVO vo = decisionMakerService.updateDecisionChain(updateDTO);

            assertNotNull(vo);
            verify(decisionChainMapper).selectById(1L);
            verify(decisionChainMapper).updateById(any(DecisionChain.class));
            verify(operationLogService).log(eq(1L), eq("decision_chain"), eq("UPDATE"), eq("更新决策链关系"));
        }

        @Test
        @DisplayName("异常测试-决策链不存在-抛出RuntimeException")
        void updateDecisionChain_notFound() {
            DecisionChainUpdateDTO updateDTO = new DecisionChainUpdateDTO();
            updateDTO.setId(999L);
            updateDTO.setProjectName("更新后的项目");

            when(decisionChainMapper.selectById(999L)).thenReturn(null);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> decisionMakerService.updateDecisionChain(updateDTO));

            assertEquals("决策链不存在", ex.getMessage());
            verify(decisionChainMapper, never()).updateById(any(DecisionChain.class));
        }
    }

    // ==================== deleteDecisionChain ====================

    @Nested
    @DisplayName("删除决策链关系测试")
    class DeleteDecisionChainTest {

        @Test
        @DisplayName("删除决策链关系-正常删除")
        void deleteDecisionChain_success() {
            when(decisionChainMapper.deleteById(1L)).thenReturn(1);

            decisionMakerService.deleteDecisionChain(1L);

            verify(decisionChainMapper).deleteById(1L);
            verify(operationLogService).log(eq(1L), eq("decision_chain"), eq("DELETE"), eq("删除决策链关系"));
        }
    }

    // ==================== archiveDecisionChain ====================

    @Nested
    @DisplayName("决策链数据归档测试")
    class ArchiveDecisionChainTest {

        @Test
        @DisplayName("决策链数据归档-正常归档")
        void archiveDecisionChain_success() {
            decisionMakerService.archiveDecisionChain(1L);

            verify(operationLogService).log(eq(1L), eq("decision_chain"), eq("ARCHIVE"), eq("决策链数据归档"));
        }
    }
}
