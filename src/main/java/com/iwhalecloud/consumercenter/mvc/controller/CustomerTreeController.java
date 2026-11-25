package com.iwhalecloud.consumercenter.mvc.controller;

import com.iwhalecloud.consumercenter.common.result.Result;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.profile.TaskCreateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.tree.CustomerTreeNodeCreateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.tree.CustomerTreeNodeUpdateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.tree.CustomerTreeHierarchyDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.tree.NodeFeedbackDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.task.TaskVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.tree.CustomerTreeNodeVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.tree.CustomerTreeStatisticsVO;
import com.iwhalecloud.consumercenter.mvc.service.CustomerTreeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户树管理
 */
@RestController
@RequestMapping("/api/customer-tree")
public class CustomerTreeController {

    @Autowired
    private CustomerTreeService customerTreeService;

    @GetMapping
    public Result<List<CustomerTreeNodeVO>> getTree(
            @RequestParam(required = false) String nodeType,
            @RequestParam(required = false) String keyword) {
        return Result.success(customerTreeService.getTree(nodeType, keyword));
    }

    @GetMapping("/filter")
    public Result<List<CustomerTreeNodeVO>> filterTree(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String customerType,
            @RequestParam(required = false) Integer level) {
        return Result.success(customerTreeService.filterTree(region, customerType, level));
    }

    @GetMapping("/{nodeId}/statistics")
    public Result<CustomerTreeStatisticsVO> getNodeStatistics(@PathVariable Long nodeId) {
        return Result.success(customerTreeService.getNodeStatistics(nodeId));
    }

    @PostMapping("/nodes")
    public Result<CustomerTreeNodeVO> createNode(@Valid @RequestBody CustomerTreeNodeCreateDTO createDTO) {
        return Result.success(customerTreeService.createNode(createDTO));
    }

    @PutMapping("/nodes/{id}")
    public Result<CustomerTreeNodeVO> updateNode(@PathVariable Long id, @Valid @RequestBody CustomerTreeNodeUpdateDTO updateDTO) {
        updateDTO.setId(id);
        return Result.success(customerTreeService.updateNode(updateDTO));
    }

    @DeleteMapping("/nodes/{id}")
    public Result<Void> deleteNode(@PathVariable Long id) {
        customerTreeService.deleteNode(id);
        return Result.success();
    }

    @PutMapping("/nodes/{id}/hierarchy")
    public Result<CustomerTreeNodeVO> adjustHierarchy(@PathVariable Long id, @Valid @RequestBody CustomerTreeHierarchyDTO hierarchyDTO) {
        return Result.success(customerTreeService.adjustHierarchy(id, hierarchyDTO));
    }

    @PostMapping("/nodes/{nodeId}/feedback")
    public Result<Void> nodeFeedback(@PathVariable Long nodeId, @RequestBody NodeFeedbackDTO feedbackDTO) {
        customerTreeService.nodeFeedback(nodeId, feedbackDTO);
        return Result.success();
    }

    @PostMapping("/nodes/{nodeId}/tasks")
    public Result<TaskVO> createTask(@PathVariable Long nodeId, @Valid @RequestBody TaskCreateDTO taskDTO) {
        taskDTO.setTreeNodeId(nodeId);
        // TODO: 调用TaskService创建任务
        TaskVO vo = new TaskVO();
        vo.setId(1L);
        vo.setTaskName(taskDTO.getTaskName());
        vo.setTaskType(taskDTO.getTaskType());
        vo.setStatus("待处理");
        return Result.success(vo);
    }
}
