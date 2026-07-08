package com.iwhalecloud.customer.mvc.service;

import com.iwhalecloud.customer.mvc.pojo.dto.profile.TaskCreateDTO;
import com.iwhalecloud.customer.mvc.pojo.vo.task.TaskVO;

/**
 * 任务服务
 */
public interface TaskService {

    /**
     * 创建任务
     */
    TaskVO createTask(TaskCreateDTO createDTO);
}
