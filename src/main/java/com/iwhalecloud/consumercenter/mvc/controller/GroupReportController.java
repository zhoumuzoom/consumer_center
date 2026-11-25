package com.iwhalecloud.consumercenter.mvc.controller;

import com.iwhalecloud.consumercenter.common.result.PageResult;
import com.iwhalecloud.consumercenter.common.result.Result;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.report.ManualReportDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.report.ReportQueryDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.report.ScheduleConfigUpdateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.report.*;
import com.iwhalecloud.consumercenter.mvc.service.GroupReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 集团数据上报管理
 */
@RestController
@RequestMapping("/api/group-report")
public class GroupReportController {

    @Autowired
    private GroupReportService groupReportService;

    @GetMapping("/overview")
    public Result<ReportOverviewVO> getOverview() {
        return Result.success(groupReportService.getOverview());
    }

    @GetMapping("/abnormal")
    public Result<PageResult<AbnormalDataVO>> pageAbnormalData(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(groupReportService.pageAbnormalData(page, size));
    }

    @PostMapping("/manual")
    public Result<ManualReportResultVO> manualReport(@RequestBody ManualReportDTO reportDTO) {
        return Result.success(groupReportService.manualReport(reportDTO));
    }

    @GetMapping("/schedule/config")
    public Result<ScheduleConfigVO> getScheduleConfig() {
        return Result.success(groupReportService.getScheduleConfig());
    }

    @PutMapping("/schedule/config")
    public Result<ScheduleConfigVO> updateScheduleConfig(@Valid @RequestBody ScheduleConfigUpdateDTO updateDTO) {
        return Result.success(groupReportService.updateScheduleConfig(updateDTO));
    }

    @GetMapping("/records")
    public Result<PageResult<GroupReportRecordVO>> pageReportRecords(ReportQueryDTO queryDTO) {
        return Result.success(groupReportService.pageReportRecords(queryDTO));
    }

    @GetMapping("/139-system/status")
    public Result<SystemStatusVO> getGroup139Status() {
        return Result.success(groupReportService.getGroup139Status());
    }
}
