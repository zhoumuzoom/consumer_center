package com.iwhalecloud.consumercenter.mvc.service.ai.tool;

import com.iwhalecloud.consumercenter.common.result.PageResult;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.decision.DecisionChainQueryDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.decision.DecisionChainVO;
import com.iwhalecloud.consumercenter.mvc.service.DecisionMakerService;
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
 * 决策链工具测试
 */
@ExtendWith(MockitoExtension.class)
class DecisionToolsTest {

    @InjectMocks
    private DecisionTools decisionTools;

    @Mock
    private DecisionMakerService decisionMakerService;

    @Test
    @DisplayName("按客户名查询决策链并限制条数")
    void getDecisionChains_returnsDecisionMakers() {
        DecisionChainVO chain = new DecisionChainVO();
        chain.setCustomerName("中国工商银行");
        chain.setProjectName("核心银行系统升级");
        chain.setDecisionMakerName("张总");
        chain.setDecisionMakerPosition("信息科技部总经理");
        chain.setDecisionStage("方案审批");
        chain.setInfluenceLevel("高");

        PageResult<DecisionChainVO> pageResult = new PageResult<>();
        pageResult.setTotal(7L);
        pageResult.setRecords(Collections.singletonList(chain));
        when(decisionMakerService.pageDecisionChains(any(DecisionChainQueryDTO.class))).thenReturn(pageResult);

        String result = decisionTools.getDecisionChains("中国工商银行", null, null);

        assertTrue(result.contains("张总"));
        assertTrue(result.contains("信息科技部总经理"));
        assertTrue(result.contains("\"returned\":1"));
        assertFalse(result.contains("\"total\":7"));
        ArgumentCaptor<DecisionChainQueryDTO> captor = ArgumentCaptor.forClass(DecisionChainQueryDTO.class);
        verify(decisionMakerService).pageDecisionChains(captor.capture());
        assertTrue(captor.getValue().getSize() <= 10);
    }
}