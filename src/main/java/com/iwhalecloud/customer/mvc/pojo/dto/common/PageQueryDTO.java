package com.iwhalecloud.customer.mvc.pojo.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 通用分页查询DTO
 */
@Data
@Schema(description = "分页查询参数")
public class PageQueryDTO {

    @Schema(description = "页码，默认1")
    private Integer page = 1;

    @Schema(description = "每页数量，默认10")
    private Integer size = 10;

    @Schema(description = "关键词搜索")
    private String keyword;
}
