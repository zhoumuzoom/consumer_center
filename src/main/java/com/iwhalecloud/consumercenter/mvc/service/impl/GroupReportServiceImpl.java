package com.iwhalecloud.consumercenter.mvc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iwhalecloud.consumercenter.common.result.PageResult;
import com.iwhalecloud.consumercenter.mvc.mapper.CustomerMapper;
import com.iwhalecloud.consumercenter.mvc.mapper.GroupReportRecordMapper;
import com.iwhalecloud.consumercenter.mvc.mapper.ReportScheduleConfigMapper;
import com.iwhalecloud.consumercenter.mvc.pojo.convert.GroupReportRecordConvert;
import com.iwhalecloud.consumercenter.mvc.pojo.convert.ReportScheduleConfigConvert;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.report.ManualReportDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.report.ReportQueryDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.report.ScheduleConfigUpdateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.Customer;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.GroupReportRecord;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.ReportScheduleConfig;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.report.*;
import com.iwhalecloud.consumercenter.mvc.service.GroupReportService;
import com.iwhalecloud.consumercenter.mvc.service.MockDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 集团数据上报服务实现
 */
@Slf4j
@Service
public class GroupReportServiceImpl implements GroupReportService {

    @Autowired
    private GroupReportRecordMapper groupReportRecordMapper;

    @Autowired
    private ReportScheduleConfigMapper reportScheduleConfigMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private MockDataService mockDataService;

    @Override
    public ReportOverviewVO getOverview() {
        List<GroupReportRecord> allRecords = groupReportRecordMapper.selectList(null);

        ReportOverviewVO overview = new ReportOverviewVO();
        overview.setTotalReports(allRecords.size());
        overview.setSuccessReports((int) allRecords.stream()
                .filter(r -> "成功".equals(r.getStatus())).count());
        overview.setFailReports((int) allRecords.stream()
                .filter(r -> "失败".equals(r.getStatus())).count());
        overview.setLastReportTime(allRecords.stream()
                .map(GroupReportRecord::getReportTime)
                .filter(t -> t != null)
                .max(LocalDateTime::compareTo)
                .orElse(null));
        overview.setAbnormalDataCount(3); // 模拟异常数据
        overview.setTotalDataCount(customerMapper.selectCount(null).intValue());

        return overview;
    }

    @Override
    public PageResult<AbnormalDataVO> pageAbnormalData(Integer page, Integer size) {
        // 模拟异常数据
        List<AbnormalDataVO> abnormalList = List.of(
                createAbnormalData(1L, "CUS001", "中国工商银行", "字段缺失", "客户联系邮箱为空", "contact_email", "", "必填"),
                createAbnormalData(2L, "CUS002", "中国银行", "格式错误", "联系电话格式不正确", "contact_phone", "010-abc", "数字格式"),
                createAbnormalData(3L, "CUS003", "中国平安保险", "数据不一致", "行业分类与编码不匹配", "industry", "金融业", "需匹配行业编码表")
        );

        PageResult<AbnormalDataVO> result = new PageResult<>();
        result.setTotal((long) abnormalList.size());
        result.setPages(1L);
        result.setCurrent((long) page);
        result.setSize((long) size);
        result.setRecords(abnormalList);
        return result;
    }

    @Override
    public ManualReportResultVO manualReport(ManualReportDTO reportDTO) {
        String batchNo = "RPT-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        List<Customer> allCustomers = customerMapper.selectList(null);
        int totalCount = allCustomers.size();
        int failCount = reportDTO.getIncludeAbnormal() ? 0 : 3;
        int successCount = totalCount - failCount;

        // 模拟上报
        mockDataService.mockGroupReport("模拟上报数据");

        // 保存上报记录
        GroupReportRecord record = new GroupReportRecord();
        record.setReportBatch(batchNo);
        record.setReportType("手动");
        record.setTotalCount(totalCount);
        record.setSuccessCount(successCount);
        record.setFailCount(failCount);
        record.setStatus(failCount > 0 ? "部分失败" : "成功");
        record.setReportTime(LocalDateTime.now());
        groupReportRecordMapper.insert(record);

        ManualReportResultVO result = new ManualReportResultVO();
        result.setReportBatch(batchNo);
        result.setTotalCount(totalCount);
        result.setSuccessCount(successCount);
        result.setFailCount(failCount);

        return result;
    }

    @Override
    public ScheduleConfigVO getScheduleConfig() {
        ReportScheduleConfig config = reportScheduleConfigMapper.selectOne(
                new LambdaQueryWrapper<ReportScheduleConfig>().last("LIMIT 1"));

        ScheduleConfigVO vo = new ScheduleConfigVO();
        if (config != null) {
            vo = ReportScheduleConfigConvert.entity2vo(config);
            vo.setCronDescription(parseCron(config.getCronExpression()));
        } else {
            vo.setEnabled(false);
            vo.setIncludeAbnormal(false);
            vo.setCronExpression("0 0 2 15 * ?");
            vo.setCronDescription("每月15日凌晨2点");
        }

        return vo;
    }

    @Override
    public ScheduleConfigVO updateScheduleConfig(ScheduleConfigUpdateDTO updateDTO) {
        ReportScheduleConfig config = reportScheduleConfigMapper.selectOne(
                new LambdaQueryWrapper<ReportScheduleConfig>().last("LIMIT 1"));

        if (config == null) {
            config = new ReportScheduleConfig();
        }

        if (updateDTO.getEnabled() != null) {
            config.setEnabled(updateDTO.getEnabled() ? (byte) 1 : (byte) 0);
        }
        if (updateDTO.getCronExpression() != null) {
            config.setCronExpression(updateDTO.getCronExpression());
        }
        if (updateDTO.getIncludeAbnormal() != null) {
            config.setIncludeAbnormal(updateDTO.getIncludeAbnormal() ? (byte) 1 : (byte) 0);
        }

        if (config.getId() != null) {
            reportScheduleConfigMapper.updateById(config);
        } else {
            reportScheduleConfigMapper.insert(config);
        }

        return getScheduleConfig();
    }

    @Override
    public PageResult<GroupReportRecordVO> pageReportRecords(ReportQueryDTO queryDTO) {
        Page<GroupReportRecord> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        LambdaQueryWrapper<GroupReportRecord> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getStatus() != null) {
            wrapper.eq(GroupReportRecord::getStatus, queryDTO.getStatus());
        }
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(GroupReportRecord::getReportTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(GroupReportRecord::getReportTime, queryDTO.getEndTime());
        }
        wrapper.orderByDesc(GroupReportRecord::getReportTime);

        Page<GroupReportRecord> result = groupReportRecordMapper.selectPage(page, wrapper);

        List<GroupReportRecordVO> voList = result.getRecords().stream()
                .map(r -> {
                    return GroupReportRecordConvert.entity2vo(r);
                })
                .collect(Collectors.toList());

        PageResult<GroupReportRecordVO> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setPages(result.getPages());
        pageResult.setCurrent(result.getCurrent());
        pageResult.setSize(result.getSize());
        pageResult.setRecords(voList);

        return pageResult;
    }

    @Override
    public SystemStatusVO getGroup139Status() {
        SystemStatusVO status = new SystemStatusVO();
        status.setConnected(true);
        status.setLastSyncTime(LocalDateTime.now().minusHours(2));
        status.setSyncStatus("success");
        status.setSystemName("集团139要客系统");
        return status;
    }

    /**
     * 定时上报任务（模拟）
     */
    @Scheduled(cron = "0 0 2 15 * ?")
    public void scheduledReport() {
        log.info("[定时任务] 开始执行定时上报...");
        ReportScheduleConfig config = reportScheduleConfigMapper.selectOne(
                new LambdaQueryWrapper<ReportScheduleConfig>().last("LIMIT 1"));

        if (config != null && config.getEnabled() != null && config.getEnabled() == (byte) 1) {
            ManualReportDTO reportDTO = new ManualReportDTO();
            reportDTO.setIncludeAbnormal(config.getIncludeAbnormal() != null && config.getIncludeAbnormal() == (byte) 1);
            manualReport(reportDTO);

            // 更新上次执行时间
            config.setLastExecuteTime(LocalDateTime.now());
            reportScheduleConfigMapper.updateById(config);
        }
        log.info("[定时任务] 定时上报执行完成");
    }

    private AbnormalDataVO createAbnormalData(Long customerId, String code, String name,
                                               String type, String desc, String field, String value, String rule) {
        AbnormalDataVO vo = new AbnormalDataVO();
        vo.setCustomerId(customerId);
        vo.setCustomerCode(code);
        vo.setCustomerName(name);
        vo.setAbnormalType(type);
        vo.setAbnormalDesc(desc);
        vo.setAbnormalField(field);
        vo.setCurrentValue(value);
        vo.setRuleRequirement(rule);
        return vo;
    }

    private String parseCron(String cron) {
        if (cron == null) return "";
        // 简单解析常见Cron表达式
        if (cron.contains("15") && cron.contains("2")) return "每月15日凌晨2点";
        if (cron.contains("0 0 2 * * ?")) return "每天凌晨2点";
        return cron;
    }
}
