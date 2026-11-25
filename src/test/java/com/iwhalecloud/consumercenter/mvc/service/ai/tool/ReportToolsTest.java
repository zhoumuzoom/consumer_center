package com.iwhalecloud.consumercenter.mvc.service.ai.tool;

import com.iwhalecloud.consumercenter.mvc.pojo.vo.report.ReportOverviewVO;
import com.iwhalecloud.consumercenter.mvc.service.GroupReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * 集团上报工具测试
 */
@ExtendWith(MockitoExtension.class)
class ReportToolsTest {

    @InjectMocks
    private ReportTools reportTools;

    @Mock
    private GroupReportService groupReportService;

    @Test
    @DisplayName("查询上报总览返回核心指标")
    void getGroupReportOverview_returnsMetrics() {
        ReportOverviewVO overview = new ReportOverviewVO();
        overview.setTotalReports(10);
        overview.setSuccessReports(9);
        overview.setFailReports(1);
        overview.setAbnormalDataCount(2);
        overview.setTotalDataCount(100);
        overview.setLastReportTime(LocalDateTime.of(2026, 11, 25, 10, 0));
        when(groupReportService.getOverview()).thenReturn(overview);

        String result = reportTools.getGroupReportOverview();

        assertTrue(result.contains("totalReports"));
        assertTrue(result.contains("10"));
        assertTrue(result.contains("abnormalDataCount"));
    }
}