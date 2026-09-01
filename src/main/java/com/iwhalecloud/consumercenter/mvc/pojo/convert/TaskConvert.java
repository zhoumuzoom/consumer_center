package com.iwhalecloud.consumercenter.mvc.pojo.convert;

import com.iwhalecloud.consumercenter.mvc.pojo.dto.profile.TaskCreateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.Task;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.task.TaskVO;

/**
 * 任务对象转换
 */
public final class TaskConvert {

    private TaskConvert() {
    }

    public static Task dto2entity(TaskCreateDTO source) {
        Task target = new Task();
        target.setTaskName(source.getTaskName());
        target.setTaskType(source.getTaskType());
        target.setTaskContent(source.getTaskContent());
        target.setPriority(source.getPriority());
        target.setTreeNodeId(source.getTreeNodeId());
        target.setCustomerId(source.getCustomerId());
        target.setAssigneeName(source.getAssigneeName());
        target.setDeadline(source.getDeadline());
        return target;
    }

    public static TaskVO entity2vo(Task source) {
        TaskVO target = new TaskVO();
        target.setId(source.getId());
        target.setTreeNodeId(source.getTreeNodeId());
        target.setCustomerId(source.getCustomerId());
        target.setTaskName(source.getTaskName());
        target.setTaskType(source.getTaskType());
        target.setTaskContent(source.getTaskContent());
        target.setPriority(source.getPriority());
        target.setAssigneeId(source.getAssigneeId());
        target.setAssigneeName(source.getAssigneeName());
        target.setStatus(source.getStatus());
        target.setDeadline(source.getDeadline());
        target.setCompletedTime(source.getCompletedTime());
        target.setCreateTime(source.getCreateTime());
        return target;
    }
}
