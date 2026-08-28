package com.iwhalecloud.consumercenter.mvc.service;

import com.iwhalecloud.consumercenter.common.result.PageResult;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.report.ManualReportDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.report.ReportQueryDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.report.ScheduleConfigUpdateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.report.AbnormalDataVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.report.GroupReportRecordVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.report.ManualReportResultVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.report.ReportOverviewVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.report.ScheduleConfigVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.report.SystemStatusVO;

/**
 * 集团数据上报服务
 */
public interface GroupReportService {

    /**
     * 获取上报数据总览
     */
    ReportOverviewVO getOverview();

    /**
     * 查询异常数据明细
     */
    PageResult<AbnormalDataVO> pageAbnormalData(Integer page, Integer size);

    /**
     * 手动同步上报
     */
    ManualReportResultVO manualReport(ManualReportDTO reportDTO);

    /**
     * 获取定时上报配置
     */
    ScheduleConfigVO getScheduleConfig();

    /**
     * 更新定时上报配置
     */
    ScheduleConfigVO updateScheduleConfig(ScheduleConfigUpdateDTO updateDTO);

    /**
     * 分页查询上报记录
     */
    PageResult<GroupReportRecordVO> pageReportRecords(ReportQueryDTO queryDTO);

    /**
     * 获取集团139系统对接状态
     */
    SystemStatusVO getGroup139Status();
}
