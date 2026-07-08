package com.iwhalecloud.customer.mvc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iwhalecloud.customer.mvc.mapper.CustomerIndustryTagMapper;
import com.iwhalecloud.customer.mvc.mapper.CustomerMapper;
import com.iwhalecloud.customer.mvc.mapper.IndustryTreeNodeMapper;
import com.iwhalecloud.customer.mvc.pojo.dto.industry.BatchTagDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.industry.CustomerIndustryTagDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.industry.IndustryTreeNodeCreateDTO;
import com.iwhalecloud.customer.mvc.pojo.entity.Customer;
import com.iwhalecloud.customer.mvc.pojo.entity.CustomerIndustryTag;
import com.iwhalecloud.customer.mvc.pojo.entity.IndustryTreeNode;
import com.iwhalecloud.customer.mvc.pojo.vo.industry.CustomerIndustryTagVO;
import com.iwhalecloud.customer.mvc.pojo.vo.industry.IndustryAggregationVO;
import com.iwhalecloud.customer.mvc.pojo.vo.industry.IndustryTreeNodeVO;
import com.iwhalecloud.customer.mvc.pojo.vo.industry.ResourceAnalysisVO;
import com.iwhalecloud.customer.mvc.service.IndustryTreeService;
import com.iwhalecloud.customer.mvc.service.OperationLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 行业树服务实现
 */
@Service
public class IndustryTreeServiceImpl implements IndustryTreeService {

    @Autowired
    private IndustryTreeNodeMapper industryTreeNodeMapper;

    @Autowired
    private CustomerIndustryTagMapper customerIndustryTagMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private OperationLogService operationLogService;

    @Override
    public List<IndustryTreeNodeVO> getTree() {
        List<IndustryTreeNode> allNodes = industryTreeNodeMapper.selectList(
                new LambdaQueryWrapper<IndustryTreeNode>().orderByAsc(IndustryTreeNode::getSortOrder));
        return buildTree(allNodes, 0L);
    }

    @Override
    public IndustryAggregationVO getNodeAggregation(Long nodeId) {
        IndustryTreeNode node = industryTreeNodeMapper.selectById(nodeId);
        if (node == null) {
            throw new RuntimeException("行业节点不存在");
        }

        // 统计该行业下的客户
        List<CustomerIndustryTag> tags = customerIndustryTagMapper.selectList(
                new LambdaQueryWrapper<CustomerIndustryTag>().eq(CustomerIndustryTag::getIndustryNodeId, nodeId));

        IndustryAggregationVO aggregation = new IndustryAggregationVO();
        aggregation.setNodeId(nodeId);
        aggregation.setNodeName(node.getNodeName());
        aggregation.setCustomerCount(tags.size());
        aggregation.setManagementRate(80.0);
        aggregation.setGroupCount((int) (tags.size() * 0.3));
        aggregation.setStrategicCount((int) tags.stream().filter(t -> "战略要客".equals(t.getTagLevel())).count());
        aggregation.setKeyCount((int) tags.stream().filter(t -> "重点客户".equals(t.getTagLevel())).count());

        return aggregation;
    }

    @Override
    public List<IndustryTreeNodeVO> drillDown(Long nodeId) {
        List<IndustryTreeNode> children = industryTreeNodeMapper.selectList(
                new LambdaQueryWrapper<IndustryTreeNode>()
                        .eq(IndustryTreeNode::getParentId, nodeId)
                        .orderByAsc(IndustryTreeNode::getSortOrder));
        return children.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public IndustryTreeNodeVO createNode(IndustryTreeNodeCreateDTO createDTO) {
        IndustryTreeNode node = new IndustryTreeNode();
        BeanUtils.copyProperties(createDTO, node);

        // 计算层级
        if (createDTO.getParentId() != null && createDTO.getParentId() > 0) {
            IndustryTreeNode parent = industryTreeNodeMapper.selectById(createDTO.getParentId());
            node.setLevel(parent != null ? parent.getLevel() + 1 : 1);
        } else {
            node.setLevel(1);
        }

        industryTreeNodeMapper.insert(node);
        operationLogService.log(node.getId(), "industry_tree_node", "CREATE",
                "新增行业节点: " + node.getNodeName());

        return convertToVO(node);
    }

    @Override
    public CustomerIndustryTagVO setCustomerTag(Long customerId, CustomerIndustryTagDTO tagDTO) {
        // 检查是否已存在
        CustomerIndustryTag existing = customerIndustryTagMapper.selectOne(
                new LambdaQueryWrapper<CustomerIndustryTag>()
                        .eq(CustomerIndustryTag::getCustomerId, customerId)
                        .eq(CustomerIndustryTag::getIndustryNodeId, tagDTO.getIndustryNodeId()));

        if (existing != null) {
            existing.setTagLevel(tagDTO.getTagLevel());
            existing.setCooperationStatus(tagDTO.getCooperationStatus());
            customerIndustryTagMapper.updateById(existing);
            return convertToTagVO(existing, customerId);
        }

        CustomerIndustryTag tag = new CustomerIndustryTag();
        tag.setCustomerId(customerId);
        tag.setIndustryNodeId(tagDTO.getIndustryNodeId());
        tag.setTagLevel(tagDTO.getTagLevel());
        tag.setCooperationStatus(tagDTO.getCooperationStatus());
        customerIndustryTagMapper.insert(tag);

        operationLogService.log(customerId, "customer_industry_tag", "CREATE",
                "设置客户行业标签");

        return convertToTagVO(tag, customerId);
    }

    @Override
    public void batchSetTags(BatchTagDTO batchDTO) {
        for (Long customerId : batchDTO.getCustomerIds()) {
            CustomerIndustryTagDTO tagDTO = new CustomerIndustryTagDTO();
            tagDTO.setIndustryNodeId(batchDTO.getIndustryNodeId());
            tagDTO.setTagLevel(batchDTO.getTagLevel());
            tagDTO.setCooperationStatus(batchDTO.getCooperationStatus());
            setCustomerTag(customerId, tagDTO);
        }
        operationLogService.log(0L, "customer_industry_tag", "BATCH_CREATE",
                "批量设置客户标签, 客户数: " + batchDTO.getCustomerIds().size());
    }

    @Override
    public CustomerIndustryTagVO updateCustomerTag(Long customerId, Long tagId, CustomerIndustryTagDTO tagDTO) {
        CustomerIndustryTag tag = customerIndustryTagMapper.selectById(tagId);
        if (tag == null || !tag.getCustomerId().equals(customerId)) {
            throw new RuntimeException("标签不存在");
        }

        tag.setTagLevel(tagDTO.getTagLevel());
        tag.setCooperationStatus(tagDTO.getCooperationStatus());
        customerIndustryTagMapper.updateById(tag);

        return convertToTagVO(tag, customerId);
    }

    @Override
    public void deleteCustomerTag(Long customerId, Long tagId) {
        customerIndustryTagMapper.deleteById(tagId);
        operationLogService.log(customerId, "customer_industry_tag", "DELETE", "删除客户行业标签");
    }

    @Override
    public ResourceAnalysisVO resourceAnalysis(Long nodeId) {
        IndustryTreeNode node = industryTreeNodeMapper.selectById(nodeId);
        if (node == null) {
            throw new RuntimeException("行业节点不存在");
        }

        List<CustomerIndustryTag> tags = customerIndustryTagMapper.selectList(
                new LambdaQueryWrapper<CustomerIndustryTag>().eq(CustomerIndustryTag::getIndustryNodeId, nodeId));

        ResourceAnalysisVO analysis = new ResourceAnalysisVO();
        analysis.setNodeId(nodeId);
        analysis.setNodeName(node.getNodeName());
        analysis.setTotalCustomers(tags.size());
        analysis.setResourceInvestment((long) (tags.size() * 50));
        analysis.setBusinessContribution((long) (tags.size() * 200));
        analysis.setContributionRate(25.0);
        analysis.setAvgCustomerValue(tags.isEmpty() ? 0.0 : 200.0);

        return analysis;
    }

    private List<IndustryTreeNodeVO> buildTree(List<IndustryTreeNode> allNodes, Long parentId) {
        return allNodes.stream()
                .filter(n -> parentId.equals(n.getParentId()))
                .map(n -> {
                    IndustryTreeNodeVO vo = convertToVO(n);
                    vo.setChildren(buildTree(allNodes, n.getId()));
                    // 统计客户数
                    long count = customerIndustryTagMapper.selectCount(
                            new LambdaQueryWrapper<CustomerIndustryTag>().eq(CustomerIndustryTag::getIndustryNodeId, n.getId()));
                    vo.setCustomerCount((int) count);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    private IndustryTreeNodeVO convertToVO(IndustryTreeNode node) {
        IndustryTreeNodeVO vo = new IndustryTreeNodeVO();
        BeanUtils.copyProperties(node, vo);
        return vo;
    }

    private CustomerIndustryTagVO convertToTagVO(CustomerIndustryTag tag, Long customerId) {
        CustomerIndustryTagVO vo = new CustomerIndustryTagVO();
        BeanUtils.copyProperties(tag, vo);
        vo.setCustomerId(customerId);

        Customer customer = customerMapper.selectById(customerId);
        if (customer != null) {
            vo.setCustomerName(customer.getCustomerName());
        }

        IndustryTreeNode industryNode = industryTreeNodeMapper.selectById(tag.getIndustryNodeId());
        if (industryNode != null) {
            vo.setIndustryName(industryNode.getNodeName());
        }

        return vo;
    }
}
