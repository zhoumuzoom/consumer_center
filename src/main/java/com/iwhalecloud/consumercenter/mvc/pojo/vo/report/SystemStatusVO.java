package com.iwhalecloud.consumercenter.mvc.pojo.vo.report;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 集团系统对接状态VO
 */
@Data
public class SystemStatusVO {

    private Boolean connected;

    private LocalDateTime lastSyncTime;

    private String syncStatus;

    private String systemName;
}
