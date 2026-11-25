package com.iwhalecloud.consumercenter.mvc.pojo.dto.customer;

import com.iwhalecloud.consumercenter.common.param.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerQueryDTO extends PageQuery {

    private String customerName;

    private String industry;

    private String region;

    private String scale;

    private Integer status;

    private String source;
}
