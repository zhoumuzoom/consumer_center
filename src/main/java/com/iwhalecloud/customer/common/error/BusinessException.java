package com.iwhalecloud.customer.common.error;

import lombok.Getter;

import java.io.Serial;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 7087132107974693888L;

    private final Integer code;

    public BusinessException(ErrorMessageEnum errorMessage) {
        super(errorMessage.getMessage());
        this.code = errorMessage.getCode();
    }

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}