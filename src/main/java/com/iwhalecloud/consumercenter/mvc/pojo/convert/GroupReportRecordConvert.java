package com.iwhalecloud.consumercenter.mvc.pojo.convert;

import com.iwhalecloud.consumercenter.mvc.pojo.entity.GroupReportRecord;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.report.GroupReportRecordVO;

/**
 * 集团上报记录对象转换
 */
public final class GroupReportRecordConvert {

    private GroupReportRecordConvert() {
    }

    public static GroupReportRecordVO entity2vo(GroupReportRecord source) {
        GroupReportRecordVO target = new GroupReportRecordVO();
        target.setId(source.getId());
        target.setReportBatch(source.getReportBatch());
        target.setReportType(source.getReportType());
        target.setTotalCount(source.getTotalCount());
        target.setSuccessCount(source.getSuccessCount());
        target.setFailCount(source.getFailCount());
        target.setStatus(source.getStatus());
        target.setErrorMsg(source.getErrorMsg());
        target.setReportTime(source.getReportTime());
        target.setCreateTime(source.getCreateTime());
        return target;
    }
}
