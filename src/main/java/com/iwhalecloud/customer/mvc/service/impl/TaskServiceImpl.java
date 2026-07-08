package com.iwhalecloud.customer.mvc.service.impl;

import com.iwhalecloud.customer.mvc.mapper.CustomerMapper;
import com.iwhalecloud.customer.mvc.mapper.CustomerTreeNodeMapper;
import com.iwhalecloud.customer.mvc.mapper.TaskMapper;
import com.iwhalecloud.customer.mvc.pojo.dto.profile.TaskCreateDTO;
import com.iwhalecloud.customer.mvc.pojo.entity.Customer;
import com.iwhalecloud.customer.mvc.pojo.entity.CustomerTreeNode;
import com.iwhalecloud.customer.mvc.pojo.entity.Task;
import com.iwhalecloud.customer.mvc.pojo.vo.task.TaskVO;
import com.iwhalecloud.customer.mvc.service.OperationLogService;
import com.iwhalecloud.customer.mvc.service.TaskService;
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
