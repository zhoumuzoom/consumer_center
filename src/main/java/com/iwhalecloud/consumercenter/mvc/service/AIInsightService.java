package com.iwhalecloud.consumercenter.mvc.service;

import com.iwhalecloud.consumercenter.mvc.pojo.vo.profile.AIInsightResultVO;

/**
 * AI洞察服务
 */
public interface AIInsightService {

    /** 提交AI洞察任务 */
    AIInsightResultVO generateAIInsight(Long customerId);

    /** 查询AI洞察任务结果 */
    AIInsightResultVO getAIInsightResult(Long customerId, String taskId);
}