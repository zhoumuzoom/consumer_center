package com.iwhalecloud.customer.mvc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iwhalecloud.customer.mvc.mapper.CustomerTreeNodeMapper;
import com.iwhalecloud.customer.mvc.pojo.dto.tree.CustomerTreeNodeCreateDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.tree.CustomerTreeNodeUpdateDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.tree.CustomerTreeHierarchyDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.tree.NodeFeedbackDTO;
import com.iwhalecloud.customer.mvc.pojo.entity.CustomerTreeNode;
import com.iwhalecloud.customer.mvc.pojo.vo.tree.CustomerTreeNodeVO;
import com.iwhalecloud.customer.mvc.pojo.vo.tree.CustomerTreeStatisticsVO;
import com.iwhalecloud.customer.mvc.service.CustomerTreeService;
import com.iwhalecloud.customer.mvc.service.OperationLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 客户树服务实现
 */
@Service
public class CustomerTreeServiceImpl implements CustomerTreeService {

    @Autowired
    private CustomerTreeNodeMapper customerTreeNodeMapper;

    @Autowired
    private OperationLogService operationLogService;

    @Override
    public List<CustomerTreeNodeVO> getTree(String nodeType, String keyword) {
        LambdaQueryWrapper<CustomerTreeNode> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(nodeType)) {
            wrapper.eq(CustomerTreeNode::getNodeType, nodeType);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(CustomerTreeNode::getNodeName, keyword);
        }
        wrapper.orderByAsc(CustomerTreeNode::getSortOrder);

        List<CustomerTreeNode> allNodes = customerTreeNodeMapper.selectList(wrapper);
        return buildTree(allNodes, 0L);
    }

    @Override
    public List<CustomerTreeNodeVO> filterTree(String region, String customerType, Integer level) {
        LambdaQueryWrapper<CustomerTreeNode> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(region)) {
            wrapper.like(CustomerTreeNode::getNodeName, region);
        }
        if (StringUtils.hasText(customerType)) {
            wrapper.eq(CustomerTreeNode::getNodeType, customerType);
        }
        if (level != null) {
            wrapper.eq(CustomerTreeNode::getLevel, level);
        }
        wrapper.orderByAsc(CustomerTreeNode::getSortOrder);

        List<CustomerTreeNode> allNodes = customerTreeNodeMapper.selectList(wrapper);
        return buildTree(allNodes, 0L);
    }

    @Override
    public CustomerTreeStatisticsVO getNodeStatistics(Long nodeId) {
        CustomerTreeNode node = customerTreeNodeMapper.selectById(nodeId);
        if (node == null) {
            throw new RuntimeException("节点不存在");
        }

        // 统计子节点数量
        long childCount = customerTreeNodeMapper.selectCount(
                new LambdaQueryWrapper<CustomerTreeNode>().eq(CustomerTreeNode::getParentId, nodeId));

        CustomerTreeStatisticsVO stats = new CustomerTreeStatisticsVO();
        stats.setNodeId(nodeId);
        stats.setNodeName(node.getNodeName());
        stats.setCustomerCount(node.getCustomerCount() != null ? node.getCustomerCount() : 0);
        stats.setChildNodeCount((int) childCount);

        // 模拟看管率
        stats.setManagementRate(75.0 + Math.random() * 20);

        return stats;
    }

    @Override
    public CustomerTreeNodeVO createNode(CustomerTreeNodeCreateDTO createDTO) {
        CustomerTreeNode node = new CustomerTreeNode();
        BeanUtils.copyProperties(createDTO, node);

        // 计算层级
        if (createDTO.getParentId() != null && createDTO.getParentId() > 0) {
            CustomerTreeNode parent = customerTreeNodeMapper.selectById(createDTO.getParentId());
            node.setLevel(parent != null ? parent.getLevel() + 1 : 1);
        } else {
            node.setLevel(1);
        }

        customerTreeNodeMapper.insert(node);

        operationLogService.log(node.getId(), "customer_tree_node", "CREATE",
                "新增客户树节点: " + node.getNodeName());

        return convertToVO(node);
    }

    @Override
    public CustomerTreeNodeVO updateNode(CustomerTreeNodeUpdateDTO updateDTO) {
        CustomerTreeNode node = customerTreeNodeMapper.selectById(updateDTO.getId());
        if (node == null) {
            throw new RuntimeException("节点不存在");
        }

        BeanUtils.copyProperties(updateDTO, node);
        customerTreeNodeMapper.updateById(node);

        operationLogService.log(node.getId(), "customer_tree_node", "UPDATE",
                "修改客户树节点: " + node.getNodeName());

        return convertToVO(node);
    }

    @Override
    public void deleteNode(Long id) {
        // 级联删除子节点
        deleteNodeRecursive(id);
        operationLogService.log(id, "customer_tree_node", "DELETE", "删除客户树节点");
    }

    private void deleteNodeRecursive(Long nodeId) {
        List<CustomerTreeNode> children = customerTreeNodeMapper.selectList(
                new LambdaQueryWrapper<CustomerTreeNode>().eq(CustomerTreeNode::getParentId, nodeId));
        for (CustomerTreeNode child : children) {
            deleteNodeRecursive(child.getId());
        }
        customerTreeNodeMapper.deleteById(nodeId);
    }

    @Override
    public CustomerTreeNodeVO adjustHierarchy(Long id, CustomerTreeHierarchyDTO hierarchyDTO) {
        CustomerTreeNode node = customerTreeNodeMapper.selectById(id);
        if (node == null) {
            throw new RuntimeException("节点不存在");
        }

        node.setParentId(hierarchyDTO.getNewParentId());
        if (hierarchyDTO.getSortOrder() != null) {
            node.setSortOrder(hierarchyDTO.getSortOrder());
        }

        // 重新计算层级
        if (hierarchyDTO.getNewParentId() != null && hierarchyDTO.getNewParentId() > 0) {
            CustomerTreeNode parent = customerTreeNodeMapper.selectById(hierarchyDTO.getNewParentId());
            node.setLevel(parent != null ? parent.getLevel() + 1 : 1);
        }

        customerTreeNodeMapper.updateById(node);

        operationLogService.log(id, "customer_tree_node", "UPDATE",
                "调整客户树层级关系，新父节点ID: " + hierarchyDTO.getNewParentId());

        return convertToVO(node);
    }

    @Override
    public void nodeFeedback(Long nodeId, NodeFeedbackDTO feedbackDTO) {
        CustomerTreeNode node = customerTreeNodeMapper.selectById(nodeId);
        if (node == null) {
            throw new RuntimeException("节点不存在");
        }

        operationLogService.log(nodeId, "customer_tree_node", "FEEDBACK",
                "节点信息反馈: " + feedbackDTO.getFeedbackContent());
    }

    private List<CustomerTreeNodeVO> buildTree(List<CustomerTreeNode> allNodes, Long parentId) {
        return allNodes.stream()
                .filter(n -> parentId.equals(n.getParentId()))
                .map(n -> {
                    CustomerTreeNodeVO vo = convertToVO(n);
                    vo.setChildren(buildTree(allNodes, n.getId()));
                    return vo;
                })
                .collect(Collectors.toList());
    }

    private CustomerTreeNodeVO convertToVO(CustomerTreeNode node) {
        CustomerTreeNodeVO vo = new CustomerTreeNodeVO();
        BeanUtils.copyProperties(node, vo);
        return vo;
    }
}
