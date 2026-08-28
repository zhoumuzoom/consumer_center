package com.iwhalecloud.consumercenter.common.error;

import com.iwhalecloud.consumercenter.common.constant.ErrorMessageEnum;
import com.iwhalecloud.consumercenter.common.result.Result;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Hidden // swagger配置需要隐藏全局异常处理器
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("；"));
        log.warn("参数校验失败: {}", message);
        return Result.fail(ErrorMessageEnum.BUSINESS_ERROR.getCode(), message);
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理静态资源未找到（如浏览器探测favicon等）
     * 返回404，不计入系统异常
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("资源不存在: {}", e.getResourcePath());
        return Result.fail(HttpStatus.NOT_FOUND.value(), "资源不存在");
    }

    /**
     * 处理其他运行时异常
     * 如空指针、数组下标越界
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.fail(ErrorMessageEnum.SERIVCE_ERROR.getCode(), ErrorMessageEnum.SERIVCE_ERROR.getMessage());
    }
}