package com.iwhalecloud.customer.mvc.service;

import com.iwhalecloud.customer.mvc.pojo.dto.tree.CustomerTreeNodeCreateDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.tree.CustomerTreeNodeUpdateDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.tree.CustomerTreeHierarchyDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.tree.NodeFeedbackDTO;
import com.iwhalecloud.customer.mvc.pojo.vo.tree.CustomerTreeNodeVO;
import com.iwhalecloud.customer.mvc.pojo.vo.tree.CustomerTreeStatisticsVO;

import java.util.List;

/**
 * 客户树服务
 */
public interface CustomerTreeService {

    /**
     * 获取完整客户树结构
     */
    List<CustomerTreeNodeVO> getTree(String nodeType, String keyword);

    /**
     * 多维度筛选客户树
     */
    List<CustomerTreeNodeVO> filterTree(String region, String customerType, Integer level);

    /**
     * 获取节点统计信息
     */
    CustomerTreeStatisticsVO getNodeStatistics(Long nodeId);

    /**
     * 新增节点
     */
    CustomerTreeNodeVO createNode(CustomerTreeNodeCreateDTO createDTO);

    /**
     * 修改节点
     */
    CustomerTreeNodeVO updateNode(CustomerTreeNodeUpdateDTO updateDTO);

    /**
     * 删除节点（级联删除子节点）
     */
    void deleteNode(Long id);

    /**
     * 调整层级关系
     */
    CustomerTreeNodeVO adjustHierarchy(Long id, CustomerTreeHierarchyDTO hierarchyDTO);

    /**
     * 节点信息反馈
     */
    void nodeFeedback(Long nodeId, NodeFeedbackDTO feedbackDTO);
}
