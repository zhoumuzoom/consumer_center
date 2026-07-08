package com.iwhalecloud.customer.mvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iwhalecloud.customer.common.error.GlobalExceptionHandler;
import com.iwhalecloud.customer.common.result.PageResult;
import com.iwhalecloud.customer.mvc.pojo.dto.customer.CustomerCreateDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.customer.CustomerStatusUpdateDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.customer.CustomerUpdateDTO;
import com.iwhalecloud.customer.mvc.pojo.vo.customer.CustomerDetailVO;
import com.iwhalecloud.customer.mvc.pojo.vo.customer.CustomerVO;
import com.iwhalecloud.customer.mvc.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 客户控制器测试
 */
@WebMvcTest(CustomerController.class)
@Import(GlobalExceptionHandler.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    private CustomerVO buildCustomerVO() {
        CustomerVO vo = new CustomerVO();
        vo.setId(1L);
        vo.setCustomerCode("CUS20260703120000ABCD");
        vo.setCustomerName("测试客户");
        vo.setCustomerNameEn("Test Customer");
        vo.setIndustry("IT");
        vo.setRegion("华东");
        vo.setScale("大型");
        vo.setStatus(0);
        vo.setStatusDesc("未看管");
        vo.setManagerName("张三");
        vo.setJoinTime(LocalDateTime.of(2026, 1, 1, 0, 0));
        vo.setSource("手动录入");
        vo.setContactPerson("李四");
        vo.setContactPhone("13800138000");
        vo.setCreateTime(LocalDateTime.of(2026, 1, 1, 0, 0));
        return vo;
    }

    private CustomerDetailVO buildCustomerDetailVO() {
        CustomerDetailVO detailVO = new CustomerDetailVO();
        CustomerVO vo = buildCustomerVO();
        detailVO.setId(vo.getId());
        detailVO.setCustomerCode(vo.getCustomerCode());
        detailVO.setCustomerName(vo.getCustomerName());
        detailVO.setCustomerNameEn(vo.getCustomerNameEn());
        detailVO.setIndustry(vo.getIndustry());
        detailVO.setRegion(vo.getRegion());
        detailVO.setScale(vo.getScale());
        detailVO.setStatus(vo.getStatus());
        detailVO.setStatusDesc(vo.getStatusDesc());
        detailVO.setManagerName(vo.getManagerName());
        detailVO.setJoinTime(vo.getJoinTime());
        detailVO.setSource(vo.getSource());
        detailVO.setContactPerson(vo.getContactPerson());
        detailVO.setContactPhone(vo.getContactPhone());
        detailVO.setCreateTime(vo.getCreateTime());
        detailVO.setContactEmail("test@example.com");
        detailVO.setAddress("上海市浦东新区");
        detailVO.setRemark("测试备注");
        detailVO.setBranchCount(3);
        detailVO.setDecisionMakerCount(2);
        detailVO.setInteractionCount(5);
        return detailVO;
    }

    // ==================== pageList ====================

    @Test
    @DisplayName("客户列表查询-正常返回分页数据")
    void pageList_success() throws Exception {
        PageResult<CustomerVO> pageResult = new PageResult<>();
        pageResult.setTotal(1L);
        pageResult.setPages(1L);
        pageResult.setCurrent(1L);
        pageResult.setSize(10L);
        pageResult.setRecords(List.of(buildCustomerVO()));

        when(customerService.pageList(any())).thenReturn(pageResult);

        mockMvc.perform(get("/api/customers")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].customerName").value("测试客户"));
    }

    @Test
    @DisplayName("客户列表查询-空结果返回空列表")
    void pageList_emptyResult() throws Exception {
        PageResult<CustomerVO> pageResult = new PageResult<>();
        pageResult.setTotal(0L);
        pageResult.setPages(0L);
        pageResult.setCurrent(1L);
        pageResult.setSize(10L);
        pageResult.setRecords(Collections.emptyList());

        when(customerService.pageList(any())).thenReturn(pageResult);

        mockMvc.perform(get("/api/customers")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.records").isEmpty());
    }

    // ==================== getDetail ====================

    @Test
    @DisplayName("客户详情查询-正常返回详情")
    void getDetail_success() throws Exception {
        when(customerService.getDetail(1L)).thenReturn(buildCustomerDetailVO());

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.customerName").value("测试客户"))
                .andExpect(jsonPath("$.data.contactEmail").value("test@example.com"))
                .andExpect(jsonPath("$.data.branchCount").value(3));
    }

    @Test
    @DisplayName("异常测试-客户详情查询时客户不存在，服务抛出RuntimeException")
    void getDetail_customerNotFound() throws Exception {
        when(customerService.getDetail(999L)).thenThrow(new RuntimeException("客户不存在"));

        mockMvc.perform(get("/api/customers/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(5001))
                .andExpect(jsonPath("$.message").value("系统异常"));
    }

    // ==================== create ====================

    @Test
    @DisplayName("新增客户-正常创建")
    void create_success() throws Exception {
        CustomerCreateDTO createDTO = new CustomerCreateDTO();
        createDTO.setCustomerName("新客户");
        createDTO.setIndustry("IT");
        createDTO.setRegion("华东");

        when(customerService.create(any(CustomerCreateDTO.class))).thenReturn(buildCustomerVO());

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.customerName").value("测试客户"));
    }

    @Test
    @DisplayName("异常测试-新增客户时客户名称为空，触发参数校验失败")
    void create_validationFail_customerNameBlank() throws Exception {
        CustomerCreateDTO createDTO = new CustomerCreateDTO();
        createDTO.setCustomerName("");

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(5002))
                .andExpect(jsonPath("$.message").exists());
    }

    // ==================== update ====================

    @Test
    @DisplayName("修改客户-正常更新")
    void update_success() throws Exception {
        CustomerUpdateDTO updateDTO = new CustomerUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setCustomerName("修改后的客户");
        updateDTO.setIndustry("金融");

        when(customerService.update(any(CustomerUpdateDTO.class))).thenReturn(buildCustomerVO());

        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.customerName").value("测试客户"));
    }

    @Test
    @DisplayName("异常测试-修改客户时客户不存在，服务抛出RuntimeException")
    void update_customerNotFound() throws Exception {
        CustomerUpdateDTO updateDTO = new CustomerUpdateDTO();
        updateDTO.setId(999L);
        updateDTO.setCustomerName("修改后的客户");

        when(customerService.update(any(CustomerUpdateDTO.class))).thenThrow(new RuntimeException("客户不存在"));

        mockMvc.perform(put("/api/customers/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(5001))
                .andExpect(jsonPath("$.message").value("系统异常"));
    }

    // ==================== delete ====================

    @Test
    @DisplayName("删除客户-正常删除")
    void delete_success() throws Exception {
        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("异常测试-删除客户时客户不存在，服务抛出RuntimeException")
    void delete_customerNotFound() throws Exception {
        doThrow(new RuntimeException("客户不存在")).when(customerService).delete(999L);

        mockMvc.perform(delete("/api/customers/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(5001))
                .andExpect(jsonPath("$.message").value("系统异常"));
    }

    // ==================== updateStatus ====================

    @Test
    @DisplayName("客户状态变更-正常变更")
    void updateStatus_success() throws Exception {
        CustomerStatusUpdateDTO statusDTO = new CustomerStatusUpdateDTO();
        statusDTO.setStatus(1);
        statusDTO.setReason("需要重点跟进");

        when(customerService.updateStatus(eq(1L), any(CustomerStatusUpdateDTO.class)))
                .thenReturn(buildCustomerVO());

        mockMvc.perform(put("/api/customers/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.customerName").value("测试客户"));
    }

    @Test
    @DisplayName("异常测试-客户状态变更时状态为空，触发参数校验失败")
    void updateStatus_validationFail_statusNull() throws Exception {
        CustomerStatusUpdateDTO statusDTO = new CustomerStatusUpdateDTO();
        // status is null, should trigger @NotNull validation

        mockMvc.perform(put("/api/customers/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(5002))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("异常测试-客户状态变更时客户不存在，服务抛出RuntimeException")
    void updateStatus_customerNotFound() throws Exception {
        CustomerStatusUpdateDTO statusDTO = new CustomerStatusUpdateDTO();
        statusDTO.setStatus(1);

        when(customerService.updateStatus(eq(999L), any(CustomerStatusUpdateDTO.class)))
                .thenThrow(new RuntimeException("客户不存在"));

        mockMvc.perform(put("/api/customers/999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(5001))
                .andExpect(jsonPath("$.message").value("系统异常"));
    }
}
