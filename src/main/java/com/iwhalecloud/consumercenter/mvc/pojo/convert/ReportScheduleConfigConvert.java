package com.iwhalecloud.consumercenter.mvc.pojo.convert;

import com.iwhalecloud.consumercenter.mvc.pojo.entity.ReportScheduleConfig;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.report.ScheduleConfigVO;

/**
 * 定时上报配置对象转换
 */
public final class ReportScheduleConfigConvert {

    private ReportScheduleConfigConvert() {
    }

    public static ScheduleConfigVO entity2vo(ReportScheduleConfig source) {
        ScheduleConfigVO target = new ScheduleConfigVO();
        target.setId(source.getId());
        target.setEnabled(source.getEnabled() != null && source.getEnabled() == (byte) 1);
        target.setCronExpression(source.getCronExpression());
        target.setIncludeAbnormal(source.getIncludeAbnormal() != null && source.getIncludeAbnormal() == (byte) 1);
        target.setLastExecuteTime(source.getLastExecuteTime());
        return target;
    }
}
