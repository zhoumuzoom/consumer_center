package com.iwhalecloud.consumercenter.mvc.service.impl;

import com.iwhalecloud.consumercenter.mvc.mapper.CustomerMapper;
import com.iwhalecloud.consumercenter.mvc.mapper.CustomerTreeNodeMapper;
import com.iwhalecloud.consumercenter.mvc.mapper.TaskMapper;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.profile.TaskCreateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.Customer;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.CustomerTreeNode;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.Task;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.task.TaskVO;
import com.iwhalecloud.consumercenter.mvc.service.OperationLogService;
import com.iwhalecloud.consumercenter.mvc.service.TaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 任务服务实现
 */
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CustomerTreeNodeMapper customerTreeNodeMapper;

    @Autowired
    private OperationLogService operationLogService;

    @Override
    public TaskVO createTask(TaskCreateDTO createDTO) {
        Task task = new Task();
        BeanUtils.copyProperties(createDTO, task);
        task.setStatus("待处理");

        taskMapper.insert(task);

        String desc = "创建任务: " + task.getTaskName();
        if (task.getCustomerId() != null) {
            Customer customer = customerMapper.selectById(task.getCustomerId());
            if (customer != null) {
                desc += ", 关联客户: " + customer.getCustomerName();
            }
        }
        if (task.getTreeNodeId() != null) {
            CustomerTreeNode node = customerTreeNodeMapper.selectById(task.getTreeNodeId());
            if (node != null) {
                desc += ", 关联节点: " + node.getNodeName();
            }
        }
        operationLogService.log(task.getId(), "task", "CREATE", desc);

        return convertToVO(task);
    }

    private TaskVO convertToVO(Task task) {
        TaskVO vo = new TaskVO();
        BeanUtils.copyProperties(task, vo);

        if (task.getCustomerId() != null) {
            Customer customer = customerMapper.selectById(task.getCustomerId());
            if (customer != null) {
                vo.setCustomerName(customer.getCustomerName());
            }
        }
        if (task.getTreeNodeId() != null) {
            CustomerTreeNode node = customerTreeNodeMapper.selectById(task.getTreeNodeId());
            if (node != null) {
                vo.setTreeNodeName(node.getNodeName());
            }
        }
        return vo;
    }
}
