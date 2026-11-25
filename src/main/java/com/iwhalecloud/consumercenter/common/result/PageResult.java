package com.iwhalecloud.consumercenter.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
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
    private static final long serialVersionUID = 1769398851949433381L;

    private Long total;

    private Long pages;

    private Long current;

    private Long size;

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