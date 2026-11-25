package com.iwhalecloud.consumercenter.mvc.pojo.vo.customer;

import lombok.Data;

/**
 * 客户导出结果
 */
@Data
public class CustomerExportVO {

    private byte[] content;

    private Integer total;

    private String filename;
}
