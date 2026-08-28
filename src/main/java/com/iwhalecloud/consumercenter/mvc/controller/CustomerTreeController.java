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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户树管理
 */
@Tag(name = "客户树管理", description = "客户树结构维护、节点运维、任务下发")
@RestController
@RequestMapping("/api/customer-tree")
public class CustomerTreeController {

    @Autowired
    private CustomerTreeService customerTreeService;

    @Operation(summary = "获取客户树结构")
    @GetMapping
    public Result<List<CustomerTreeNodeVO>> getTree(
            @RequestParam(required = false) String nodeType,
            @RequestParam(required = false) String keyword) {
        return Result.success(customerTreeService.getTree(nodeType, keyword));
    }

    @Operation(summary = "客户树多维度筛选")
    @GetMapping("/filter")
    public Result<List<CustomerTreeNodeVO>> filterTree(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String customerType,
            @RequestParam(required = false) Integer level) {
        return Result.success(customerTreeService.filterTree(region, customerType, level));
    }

    @Operation(summary = "客户树节点数据统计")
    @GetMapping("/{nodeId}/statistics")
    public Result<CustomerTreeStatisticsVO> getNodeStatistics(@PathVariable Long nodeId) {
        return Result.success(customerTreeService.getNodeStatistics(nodeId));
    }

    @Operation(summary = "新增客户树节点")
    @PostMapping("/nodes")
    public Result<CustomerTreeNodeVO> createNode(@Valid @RequestBody CustomerTreeNodeCreateDTO createDTO) {
        return Result.success(customerTreeService.createNode(createDTO));
    }

    @Operation(summary = "修改客户树节点")
    @PutMapping("/nodes/{id}")
    public Result<CustomerTreeNodeVO> updateNode(@PathVariable Long id, @Valid @RequestBody CustomerTreeNodeUpdateDTO updateDTO) {
        updateDTO.setId(id);
        return Result.success(customerTreeService.updateNode(updateDTO));
    }

    @Operation(summary = "删除客户树节点")
    @DeleteMapping("/nodes/{id}")
    public Result<Void> deleteNode(@PathVariable Long id) {
        customerTreeService.deleteNode(id);
        return Result.success();
    }

    @Operation(summary = "调整客户树层级关系")
    @PutMapping("/nodes/{id}/hierarchy")
    public Result<CustomerTreeNodeVO> adjustHierarchy(@PathVariable Long id, @Valid @RequestBody CustomerTreeHierarchyDTO hierarchyDTO) {
        return Result.success(customerTreeService.adjustHierarchy(id, hierarchyDTO));
    }

    @Operation(summary = "客户树节点信息反馈")
    @PostMapping("/nodes/{nodeId}/feedback")
    public Result<Void> nodeFeedback(@PathVariable Long nodeId, @RequestBody NodeFeedbackDTO feedbackDTO) {
        customerTreeService.nodeFeedback(nodeId, feedbackDTO);
        return Result.success();
    }

    @Operation(summary = "客户树节点任务下发")
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
