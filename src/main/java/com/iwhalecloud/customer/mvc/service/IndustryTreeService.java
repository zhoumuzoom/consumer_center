package com.iwhalecloud.customer.mvc.service;

import com.iwhalecloud.customer.mvc.pojo.dto.industry.BatchTagDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.industry.CustomerIndustryTagDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.industry.IndustryTreeNodeCreateDTO;
import com.iwhalecloud.customer.mvc.pojo.vo.industry.CustomerIndustryTagVO;
import com.iwhalecloud.customer.mvc.pojo.vo.industry.IndustryAggregationVO;
import com.iwhalecloud.customer.mvc.pojo.vo.industry.IndustryTreeNodeVO;
import com.iwhalecloud.customer.mvc.pojo.vo.industry.ResourceAnalysisVO;

import java.util.List;

/**
 * 行业树服务
 */
public interface IndustryTreeService {

    /**
     * 获取完整行业树结构
     */
    List<IndustryTreeNodeVO> getTree();

    /**
     * 获取节点聚合数据
     */
    IndustryAggregationVO getNodeAggregation(Long nodeId);

    /**
     * 节点下钻
     */
    List<IndustryTreeNodeVO> drillDown(Long nodeId);

    /**
     * 新增行业节点
     */
    IndustryTreeNodeVO createNode(IndustryTreeNodeCreateDTO createDTO);

    /**
     * 设置客户行业标签
     */
    CustomerIndustryTagVO setCustomerTag(Long customerId, CustomerIndustryTagDTO tagDTO);

    /**
     * 批量设置客户标签
     */
    void batchSetTags(BatchTagDTO batchDTO);

    /**
     * 修改客户行业标签
     */
    CustomerIndustryTagVO updateCustomerTag(Long customerId, Long tagId, CustomerIndustryTagDTO tagDTO);

    /**
     * 删除客户行业标签
     */
    void deleteCustomerTag(Long customerId, Long tagId);

    /**
     * 资源穿透分析
     */
    ResourceAnalysisVO resourceAnalysis(Long nodeId);
}
