package com.iwhalecloud.customer.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 */
@Data
public class PageResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "总数")
    private Long total;

    @Schema(description = "总页面")
    private Long pages;

    @Schema(description = "当前页码")
    private Long current;

    @Schema(description = "每页大小")
    private Long size;

    @Schema(description = "数据列表")
    private List<T> records;

    public static <T> PageResult<T> of(IPage<T> iPage) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(iPage.getTotal());
        result.setPages(iPage.getPages());
        result.setCurrent(iPage.getCurrent());
        result.setSize(iPage.getSize());
        result.setRecords(iPage.getRecords());
        return result;
    }
}