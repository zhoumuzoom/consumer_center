package com.iwhalecloud.customer.mvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iwhalecloud.customer.common.error.GlobalExceptionHandler;
import com.iwhalecloud.customer.common.result.PageResult;
import com.iwhalecloud.customer.mvc.pojo.dto.decision.DecisionChainCreateDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.decision.DecisionChainUpdateDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.decision.DecisionMakerCreateDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.decision.DecisionMakerUpdateDTO;
import com.iwhalecloud.customer.mvc.pojo.vo.decision.DecisionChainVO;
import com.iwhalecloud.customer.mvc.pojo.vo.decision.DecisionMakerVO;
import com.iwhalecloud.customer.mvc.service.DecisionMakerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 决策关键人控制器测试
 */
@WebMvcTest(DecisionMakerController.class)
@Import(GlobalExceptionHandler.class)
class DecisionMakerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DecisionMakerService decisionMakerService;

    private DecisionChainVO buildDecisionChainVO() {
        DecisionChainVO vo = new DecisionChainVO();
        vo.setId(1L);
        vo.setCustomerId(10L);
        vo.setCustomerName("测试客户");
        vo.setDecisionMakerId(100L);
        vo.setDecisionMakerName("张三");
        vo.setDecisionMakerPosition("CTO");
        vo.setProjectName("智慧城市项目");
        vo.setDecisionStage("方案审批");
        vo.setInfluenceLevel("高");
        vo.setSortOrder(1);
        vo.setRemark("测试备注");
        vo.setCreateTime(LocalDateTime.of(2026, 1, 1, 0, 0));
        return vo;
    }

    private DecisionMakerVO buildDecisionMakerVO() {
        DecisionMakerVO vo = new DecisionMakerVO();
        vo.setId(100L);
        vo.setName("张三");
        vo.setPosition("CTO");
        vo.setDepartment("技术部");
        vo.setPhone("13800138000");
        vo.setEmail("zhangsan@example.com");
        vo.setRole("方案审批");
        vo.setCompany("测试公司");
        vo.setCreateTime(LocalDateTime.of(2026, 1, 1, 0, 0));
        return vo;
    }

    // ==================== pageDecisionChains ====================

    @Test
    @DisplayName("决策链列表查询-正常返回分页数据")
    void pageDecisionChains_success() throws Exception {
        PageResult<DecisionChainVO> pageResult = new PageResult<>();
        pageResult.setTotal(1L);
        pageResult.setPages(1L);
        pageResult.setCurrent(1L);
        pageResult.setSize(10L);
        pageResult.setRecords(List.of(buildDecisionChainVO()));

        when(decisionMakerService.pageDecisionChains(any())).thenReturn(pageResult);

        mockMvc.perform(get("/api/decision-chains")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].projectName").value("智慧城市项目"));
    }

    @Test
    @DisplayName("决策链列表查询-空结果")
    void pageDecisionChains_emptyResult() throws Exception {
        PageResult<DecisionChainVO> pageResult = new PageResult<>();
        pageResult.setTotal(0L);
        pageResult.setPages(0L);
        pageResult.setCurrent(1L);
        pageResult.setSize(10L);
        pageResult.setRecords(Collections.emptyList());

        when(decisionMakerService.pageDecisionChains(any())).thenReturn(pageResult);

        mockMvc.perform(get("/api/decision-chains")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0))
                .andExpect(jsonPath("$.data.records").isEmpty());
    }

    // ==================== getDecisionChainDetail ====================

    @Test
    @DisplayName("决策链详情查询-正常返回")
    void getDecisionChainDetail_success() throws Exception {
        when(decisionMakerService.getDecisionChainDetail(1L)).thenReturn(buildDecisionChainVO());

        mockMvc.perform(get("/api/decision-chains/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.projectName").value("智慧城市项目"))
                .andExpect(jsonPath("$.data.decisionMakerName").value("张三"));
    }

    @Test
    @DisplayName("异常测试-决策链不存在时服务抛出RuntimeException")
    void getDecisionChainDetail_notFound() throws Exception {
        when(decisionMakerService.getDecisionChainDetail(999L)).thenThrow(new RuntimeException("决策链不存在"));

        mockMvc.perform(get("/api/decision-chains/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(5001))
                .andExpect(jsonPath("$.message").value("系统异常"));
    }

    // ==================== createDecisionMaker ====================

    @Test
    @DisplayName("新增决策人-正常创建")
    void createDecisionMaker_success() throws Exception {
        DecisionMakerCreateDTO createDTO = new DecisionMakerCreateDTO();
        createDTO.setName("张三");
        createDTO.setPosition("CTO");
        createDTO.setDepartment("技术部");
        createDTO.setPhone("13800138000");
        createDTO.setEmail("zhangsan@example.com");
        createDTO.setRole("方案审批");
        createDTO.setCompany("测试公司");

        when(decisionMakerService.createDecisionMaker(any(DecisionMakerCreateDTO.class)))
                .thenReturn(buildDecisionMakerVO());

        mockMvc.perform(post("/api/decision-makers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("张三"))
                .andExpect(jsonPath("$.data.position").value("CTO"));
    }

    @Test
    @DisplayName("异常测试-新增决策人时姓名为空，触发参数校验失败")
    void createDecisionMaker_validationFail_nameBlank() throws Exception {
        DecisionMakerCreateDTO createDTO = new DecisionMakerCreateDTO();
        // name is blank, should trigger @NotBlank validation

        mockMvc.perform(post("/api/decision-makers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(5002))
                .andExpect(jsonPath("$.message").exists());
    }

    // ==================== updateDecisionMaker ====================

    @Test
    @DisplayName("修改决策人-正常更新")
    void updateDecisionMaker_success() throws Exception {
        DecisionMakerUpdateDTO updateDTO = new DecisionMakerUpdateDTO();
        updateDTO.setId(100L);
        updateDTO.setName("李四");
        updateDTO.setPosition("CEO");

        when(decisionMakerService.updateDecisionMaker(any(DecisionMakerUpdateDTO.class)))
                .thenReturn(buildDecisionMakerVO());

        mockMvc.perform(put("/api/decision-makers/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("张三"));
    }

    @Test
    @DisplayName("异常测试-修改决策人时决策人不存在，服务抛出RuntimeException")
    void updateDecisionMaker_notFound() throws Exception {
        DecisionMakerUpdateDTO updateDTO = new DecisionMakerUpdateDTO();
        updateDTO.setId(999L);
        updateDTO.setName("李四");

        when(decisionMakerService.updateDecisionMaker(any(DecisionMakerUpdateDTO.class)))
                .thenThrow(new RuntimeException("决策人不存在"));

        mockMvc.perform(put("/api/decision-makers/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(5001))
                .andExpect(jsonPath("$.message").value("系统异常"));
    }

    // ==================== deleteDecisionMaker ====================

    @Test
    @DisplayName("删除决策人-正常删除")
    void deleteDecisionMaker_success() throws Exception {
        mockMvc.perform(delete("/api/decision-makers/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("异常测试-删除决策人时服务抛出RuntimeException")
    void deleteDecisionMaker_exception() throws Exception {
        doThrow(new RuntimeException("删除失败")).when(decisionMakerService).deleteDecisionMaker(999L);

        mockMvc.perform(delete("/api/decision-makers/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(5001))
                .andExpect(jsonPath("$.message").value("系统异常"));
    }

    // ==================== createDecisionChain ====================

    @Test
    @DisplayName("建立决策链关系-正常创建")
    void createDecisionChain_success() throws Exception {
        DecisionChainCreateDTO createDTO = new DecisionChainCreateDTO();
        createDTO.setCustomerId(10L);
        createDTO.setDecisionMakerId(100L);
        createDTO.setProjectName("智慧城市项目");
        createDTO.setDecisionStage("方案审批");
        createDTO.setInfluenceLevel("高");
        createDTO.setSortOrder(1);
        createDTO.setRemark("测试备注");

        when(decisionMakerService.createDecisionChain(any(DecisionChainCreateDTO.class)))
                .thenReturn(buildDecisionChainVO());

        mockMvc.perform(post("/api/decision-chains")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.projectName").value("智慧城市项目"));
    }

    @Test
    @DisplayName("异常测试-建立决策链关系时客户ID为空，触发参数校验失败")
    void createDecisionChain_validationFail_customerIdNull() throws Exception {
        DecisionChainCreateDTO createDTO = new DecisionChainCreateDTO();
        createDTO.setDecisionMakerId(100L);
        // customerId is null, should trigger @NotNull validation

        mockMvc.perform(post("/api/decision-chains")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(5002))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("异常测试-建立决策链关系时决策人ID为空，触发参数校验失败")
    void createDecisionChain_validationFail_decisionMakerIdNull() throws Exception {
        DecisionChainCreateDTO createDTO = new DecisionChainCreateDTO();
        createDTO.setCustomerId(10L);
        // decisionMakerId is null, should trigger @NotNull validation

        mockMvc.perform(post("/api/decision-chains")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(5002))
                .andExpect(jsonPath("$.message").exists());
    }

    // ==================== updateDecisionChain ====================

    @Test
    @DisplayName("更新决策链关系-正常更新")
    void updateDecisionChain_success() throws Exception {
        DecisionChainUpdateDTO updateDTO = new DecisionChainUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setProjectName("更新后的项目");
        updateDTO.setDecisionStage("项目实施");

        when(decisionMakerService.updateDecisionChain(any(DecisionChainUpdateDTO.class)))
                .thenReturn(buildDecisionChainVO());

        mockMvc.perform(put("/api/decision-chains/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.projectName").value("智慧城市项目"));
    }

    @Test
    @DisplayName("异常测试-更新决策链关系时决策链不存在，服务抛出RuntimeException")
    void updateDecisionChain_notFound() throws Exception {
        DecisionChainUpdateDTO updateDTO = new DecisionChainUpdateDTO();
        updateDTO.setId(999L);
        updateDTO.setProjectName("更新后的项目");

        when(decisionMakerService.updateDecisionChain(any(DecisionChainUpdateDTO.class)))
                .thenThrow(new RuntimeException("决策链不存在"));

        mockMvc.perform(put("/api/decision-chains/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(5001))
                .andExpect(jsonPath("$.message").value("系统异常"));
    }

    // ==================== deleteDecisionChain ====================

    @Test
    @DisplayName("删除决策链关系-正常删除")
    void deleteDecisionChain_success() throws Exception {
        mockMvc.perform(delete("/api/decision-chains/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("异常测试-删除决策链关系时服务抛出RuntimeException")
    void deleteDecisionChain_exception() throws Exception {
        doThrow(new RuntimeException("删除失败")).when(decisionMakerService).deleteDecisionChain(999L);

        mockMvc.perform(delete("/api/decision-chains/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(5001))
                .andExpect(jsonPath("$.message").value("系统异常"));
    }

    // ==================== archiveDecisionChain ====================

    @Test
    @DisplayName("决策链数据归档-正常归档")
    void archiveDecisionChain_success() throws Exception {
        mockMvc.perform(post("/api/decision-chains/1/archive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("异常测试-决策链数据归档时服务抛出RuntimeException")
    void archiveDecisionChain_exception() throws Exception {
        doThrow(new RuntimeException("归档失败")).when(decisionMakerService).archiveDecisionChain(999L);

        mockMvc.perform(post("/api/decision-chains/999/archive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(5001))
                .andExpect(jsonPath("$.message").value("系统异常"));
    }
}
