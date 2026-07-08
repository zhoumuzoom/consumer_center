package com.iwhalecloud.customer.mvc.pojo.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 集团系统对接状态VO
 */
@Data
@Schema(description = "集团系统对接状态")
public class SystemStatusVO {

    @Schema(description = "是否已连接")
    private Boolean connected;

    @Schema(description = "最后同步时间")
    private LocalDateTime lastSyncTime;

    @Schema(description = "同步状态")
    private String syncStatus;

    @Schema(description = "系统名称")
    private String systemName;
}
