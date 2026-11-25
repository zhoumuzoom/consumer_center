package com.iwhalecloud.consumercenter.mvc.service.ai.tool;

import com.alibaba.fastjson2.JSON;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.report.ReportOverviewVO;
import com.iwhalecloud.consumercenter.mvc.service.GroupReportService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 集团上报工具
 */
@Component
public class ReportTools {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private GroupReportService groupReportService;

    @Tool(name = "getGroupReportOverview", description = "查询集团数据上报总览，包含上报总次数、成功/失败次数、异常数据量、总数据量和最后上报时间。用户询问上报情况时使用。")
    public String getGroupReportOverview() {
        ReportOverviewVO overview = groupReportService.getOverview();
        if (overview == null) {
            return "暂无集团上报数据";
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalReports", overview.getTotalReports());
        result.put("successReports", overview.getSuccessReports());
        result.put("failReports", overview.getFailReports());
        result.put("abnormalDataCount", overview.getAbnormalDataCount());
        result.put("totalDataCount", overview.getTotalDataCount());
        result.put("lastReportTime", overview.getLastReportTime() == null ? null
                : overview.getLastReportTime().format(DATE_FORMATTER));
        return JSON.toJSONString(result);
    }
}