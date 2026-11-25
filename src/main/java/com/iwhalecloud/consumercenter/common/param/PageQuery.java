package com.iwhalecloud.consumercenter.common.param;

import jakarta.validation.constraints.Max;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用分页查询DTO
 */
@Data
public class PageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = -7409686418451910555L;

    private Integer page = 1;

    @Max(value = 10, message = "每页数量不能超过10")
    private Integer size = 10;

    private String keyword;
}
