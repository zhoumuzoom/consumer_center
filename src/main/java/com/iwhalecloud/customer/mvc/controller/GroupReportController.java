package com.iwhalecloud.customer.mvc.controller;

import com.iwhalecloud.customer.common.result.PageResult;
import com.iwhalecloud.customer.common.result.Result;
import com.iwhalecloud.customer.mvc.pojo.dto.report.ManualReportDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.report.ReportQueryDTO;
import com.iwhalecloud.customer.mvc.pojo.dto.report.ScheduleConfigUpdateDTO;
import com.iwhalecloud.customer.mvc.pojo.vo.report.*;
import com.iwhalecloud.customer.mvc.service.GroupReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 集团数据上报管理
 */
@Tag(name = "集团数据上报管理", description = "数据上报监控、同步上报、系统对接")
@RestController
@RequestMapping("/api/group-report")
public class GroupReportController {

    @Autowired
    private GroupReportService groupReportService;

    @Operation(summary = "上报数据可视化总览")
    @GetMapping("/overview")
    public Result<ReportOverviewVO> getOverview() {
        return Result.success(groupReportService.getOverview());
    }

    @Operation(summary = "异常上报明细查询")
    @GetMapping("/abnormal")
    public Result<PageResult<AbnormalDataVO>> pageAbnormalData(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(groupReportService.pageAbnormalData(page, size));
    }

    @Operation(summary = "手动同步上报")
    @PostMapping("/manual")
    public Result<ManualReportResultVO> manualReport(@RequestBody ManualReportDTO reportDTO) {
        return Result.success(groupReportService.manualReport(reportDTO));
    }

    @Operation(summary = "获取定时上报配置")
    @GetMapping("/schedule/config")
    public Result<ScheduleConfigVO> getScheduleConfig() {
        return Result.success(groupReportService.getScheduleConfig());
    }

    @Operation(summary = "更新定时上报配置")
    @PutMapping("/schedule/config")
    public Result<ScheduleConfigVO> updateScheduleConfig(@Valid @RequestBody ScheduleConfigUpdateDTO updateDTO) {
        return Result.success(groupReportService.updateScheduleConfig(updateDTO));
    }

    @Operation(summary = "上报记录查询")
    @GetMapping("/records")
    public Result<PageResult<GroupReportRecordVO>> pageReportRecords(ReportQueryDTO queryDTO) {
        return Result.success(groupReportService.pageReportRecords(queryDTO));
    }

    @Operation(summary = "集团139系统对接状态")
    @GetMapping("/139-system/status")
    public Result<SystemStatusVO> getGroup139Status() {
        return Result.success(groupReportService.getGroup139Status());
    }
}
