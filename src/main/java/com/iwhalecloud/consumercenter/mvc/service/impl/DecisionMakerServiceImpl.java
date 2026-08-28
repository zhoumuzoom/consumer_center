package com.iwhalecloud.consumercenter.mvc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iwhalecloud.consumercenter.common.result.PageResult;
import com.iwhalecloud.consumercenter.mvc.mapper.CustomerMapper;
import com.iwhalecloud.consumercenter.mvc.mapper.DecisionChainMapper;
import com.iwhalecloud.consumercenter.mvc.mapper.DecisionMakerMapper;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.decision.*;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.Customer;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.DecisionChain;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.DecisionMaker;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.decision.DecisionChainVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.decision.DecisionMakerVO;
import com.iwhalecloud.consumercenter.mvc.service.DecisionMakerService;
import com.iwhalecloud.consumercenter.mvc.service.OperationLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 决策关键人服务实现
 */
@Service
public class DecisionMakerServiceImpl implements DecisionMakerService {

    @Autowired
    private DecisionMakerMapper decisionMakerMapper;

    @Autowired
    private DecisionChainMapper decisionChainMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private OperationLogService operationLogService;

    @Override
    public PageResult<DecisionChainVO> pageDecisionChains(DecisionChainQueryDTO queryDTO) {
        Page<DecisionChain> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        LambdaQueryWrapper<DecisionChain> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getProjectName())) {
            wrapper.like(DecisionChain::getProjectName, queryDTO.getProjectName());
        }

        wrapper.orderByAsc(DecisionChain::getSortOrder);
        Page<DecisionChain> result = decisionChainMapper.selectPage(page, wrapper);

        // 过滤客户名称和决策人名称（需要关联查询）
        List<DecisionChainVO> voList = result.getRecords().stream()
                .map(this::convertToChainVO)
                .filter(vo -> {
                    if (StringUtils.hasText(queryDTO.getCustomerName())) {
                        return vo.getCustomerName() != null && vo.getCustomerName().contains(queryDTO.getCustomerName());
                    }
                    return true;
                })
                .filter(vo -> {
                    if (StringUtils.hasText(queryDTO.getDecisionMakerName())) {
                        return vo.getDecisionMakerName() != null && vo.getDecisionMakerName().contains(queryDTO.getDecisionMakerName());
                    }
                    return true;
                })
                .filter(vo -> {
                    if (StringUtils.hasText(queryDTO.getPosition())) {
                        return vo.getDecisionMakerPosition() != null && vo.getDecisionMakerPosition().contains(queryDTO.getPosition());
                    }
                    return true;
                })
                .collect(Collectors.toList());

        PageResult<DecisionChainVO> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setPages(result.getPages());
        pageResult.setCurrent(result.getCurrent());
        pageResult.setSize(result.getSize());
        pageResult.setRecords(voList);

        return pageResult;
    }

    @Override
    public DecisionChainVO getDecisionChainDetail(Long id) {
        DecisionChain chain = decisionChainMapper.selectById(id);
        if (chain == null) {
            throw new RuntimeException("决策链不存在");
        }
        return convertToChainVO(chain);
    }

    @Override
    public DecisionMakerVO createDecisionMaker(DecisionMakerCreateDTO createDTO) {
        DecisionMaker maker = new DecisionMaker();
        BeanUtils.copyProperties(createDTO, maker);
        decisionMakerMapper.insert(maker);

        operationLogService.log(maker.getId(), "decision_maker", "CREATE",
                "新增决策人: " + maker.getName());

        return convertToMakerVO(maker);
    }

    @Override
    public DecisionMakerVO updateDecisionMaker(DecisionMakerUpdateDTO updateDTO) {
        DecisionMaker maker = decisionMakerMapper.selectById(updateDTO.getId());
        if (maker == null) {
            throw new RuntimeException("决策人不存在");
        }
        BeanUtils.copyProperties(updateDTO, maker);
        decisionMakerMapper.updateById(maker);

        operationLogService.log(maker.getId(), "decision_maker", "UPDATE",
                "修改决策人: " + maker.getName());

        return convertToMakerVO(maker);
    }

    @Override
    public void deleteDecisionMaker(Long id) {
        decisionMakerMapper.deleteById(id);
        operationLogService.log(id, "decision_maker", "DELETE", "删除决策人");
    }

    @Override
    public DecisionChainVO createDecisionChain(DecisionChainCreateDTO createDTO) {
        DecisionChain chain = new DecisionChain();
        BeanUtils.copyProperties(createDTO, chain);
        decisionChainMapper.insert(chain);

        operationLogService.log(chain.getId(), "decision_chain", "CREATE",
                "建立决策链关系");

        return convertToChainVO(chain);
    }

    @Override
    public DecisionChainVO updateDecisionChain(DecisionChainUpdateDTO updateDTO) {
        DecisionChain chain = decisionChainMapper.selectById(updateDTO.getId());
        if (chain == null) {
            throw new RuntimeException("决策链不存在");
        }
        BeanUtils.copyProperties(updateDTO, chain);
        decisionChainMapper.updateById(chain);

        operationLogService.log(chain.getId(), "decision_chain", "UPDATE", "更新决策链关系");

        return convertToChainVO(chain);
    }

    @Override
    public void deleteDecisionChain(Long id) {
        decisionChainMapper.deleteById(id);
        operationLogService.log(id, "decision_chain", "DELETE", "删除决策链关系");
    }

    @Override
    public void archiveDecisionChain(Long id) {
        operationLogService.log(id, "decision_chain", "ARCHIVE", "决策链数据归档");
    }

    private DecisionChainVO convertToChainVO(DecisionChain chain) {
        DecisionChainVO vo = new DecisionChainVO();
        BeanUtils.copyProperties(chain, vo);

        Customer customer = customerMapper.selectById(chain.getCustomerId());
        if (customer != null) {
            vo.setCustomerName(customer.getCustomerName());
        }

        DecisionMaker maker = decisionMakerMapper.selectById(chain.getDecisionMakerId());
        if (maker != null) {
            vo.setDecisionMakerName(maker.getName());
            vo.setDecisionMakerPosition(maker.getPosition());
        }

        return vo;
    }

    private DecisionMakerVO convertToMakerVO(DecisionMaker maker) {
        DecisionMakerVO vo = new DecisionMakerVO();
        BeanUtils.copyProperties(maker, vo);
        return vo;
    }
}
