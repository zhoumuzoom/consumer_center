package com.iwhalecloud.customer.mvc.service;

import com.iwhalecloud.customer.common.result.PageResult;
import com.iwhalecloud.customer.mvc.pojo.dto.decision.DecisionChainCreateDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.decision.DecisionChainQueryDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.decision.DecisionChainUpdateDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.decision.DecisionMakerCreateDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.decision.DecisionMakerUpdateDTO;
import com.iwhalecloud.customer.mvc.pojo.vo.decision.DecisionChainVO;
import com.iwhalecloud.customer.mvc.pojo.vo.decision.DecisionMakerVO;

/**
 * 决策关键人服务
 */
public interface DecisionMakerService {

    /**
     * 分页查询决策链
     */
    PageResult<DecisionChainVO> pageDecisionChains(DecisionChainQueryDTO queryDTO);

    /**
     * 查询决策链详情
     */
    DecisionChainVO getDecisionChainDetail(Long id);

    /**
     * 新增决策人
     */
    DecisionMakerVO createDecisionMaker(DecisionMakerCreateDTO createDTO);

    /**
     * 修改决策人
     */
    DecisionMakerVO updateDecisionMaker(DecisionMakerUpdateDTO updateDTO);

    /**
     * 删除决策人
     */
    void deleteDecisionMaker(Long id);

    /**
     * 建立决策链关系
     */
    DecisionChainVO createDecisionChain(DecisionChainCreateDTO createDTO);

    /**
     * 更新决策链关系
     */
    DecisionChainVO updateDecisionChain(DecisionChainUpdateDTO updateDTO);

    /**
     * 删除决策链关系
     */
    void deleteDecisionChain(Long id);

    /**
     * 决策链数据归档
     */
    void archiveDecisionChain(Long id);
}
