package com.iwhalecloud.consumercenter.common.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用分页查询DTO
 */
@Data
@Schema(description = "分页查询参数")
public class PageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = -7409686418451910555L;

    @Schema(description = "当前页码", defaultValue = "1")
    private Integer page = 1;

    @Schema(description = "每页数量", defaultValue = "10")
    @Max(value = 10, message = "每页数量不能超过10")
    private Integer size = 10;

    @Schema(description = "关键词搜索")
    private String keyword;
}
