package com.iwhalecloud.consumercenter.mvc.pojo.convert;

import com.iwhalecloud.consumercenter.mvc.pojo.dto.decision.DecisionChainCreateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.decision.DecisionChainUpdateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.DecisionChain;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.decision.DecisionChainVO;

/**
 * 决策链对象转换
 */
public final class DecisionChainConvert {

    private DecisionChainConvert() {
    }

    public static DecisionChain dto2entity(DecisionChainCreateDTO source) {
        DecisionChain target = new DecisionChain();
        target.setCustomerId(source.getCustomerId());
        target.setDecisionMakerId(source.getDecisionMakerId());
        target.setProjectName(source.getProjectName());
        target.setDecisionStage(source.getDecisionStage());
        target.setInfluenceLevel(source.getInfluenceLevel());
        target.setSortOrder(source.getSortOrder());
        target.setRemark(source.getRemark());
        return target;
    }

    public static void dto2entity(DecisionChainUpdateDTO source, DecisionChain target) {
        target.setId(source.getId());
        target.setProjectName(source.getProjectName());
        target.setDecisionStage(source.getDecisionStage());
        target.setInfluenceLevel(source.getInfluenceLevel());
        target.setSortOrder(source.getSortOrder());
        target.setRemark(source.getRemark());
    }

    public static DecisionChainVO entity2vo(DecisionChain source) {
        DecisionChainVO target = new DecisionChainVO();
        target.setId(source.getId());
        target.setCustomerId(source.getCustomerId());
        target.setDecisionMakerId(source.getDecisionMakerId());
        target.setProjectName(source.getProjectName());
        target.setDecisionStage(source.getDecisionStage());
        target.setInfluenceLevel(source.getInfluenceLevel());
        target.setSortOrder(source.getSortOrder());
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        return target;
    }
}
