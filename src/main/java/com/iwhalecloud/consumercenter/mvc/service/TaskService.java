package com.iwhalecloud.consumercenter.mvc.service;

import com.iwhalecloud.consumercenter.mvc.pojo.dto.profile.TaskCreateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.task.TaskVO;

/**
 * 任务服务
 */
public interface TaskService {

    /**
     * 创建任务
     */
    TaskVO createTask(TaskCreateDTO createDTO);
}
