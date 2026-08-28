package com.iwhalecloud.consumercenter.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * API日志切面
 * 监控接口访问，记录请求信息和耗时
 */
@Slf4j
@Aspect
@Component
public class ApiLogAspect {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 切入点：所有Controller方法
     */
    @Pointcut("execution(* com.iwhalecloud.consumercenter.mvc.controller..*(..))")
    public void apiPointcut() {
    }

    /**
     * 环绕通知
     *
     * @param joinPoint 切点
     * @return 接口返回
     * @throws Throwable 接口异常
     */
    @Around("apiPointcut()")
    public Object aroundApi(ProceedingJoinPoint joinPoint) throws Throwable {
        // 前置通知
        // 记录开始时间
        LocalDateTime startTime = LocalDateTime.now();
        long startMillis = System.currentTimeMillis();

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            log.info("未获取到请求信息，直接调用方法");
            return joinPoint.proceed(joinPoint.getArgs());
        }
        HttpServletRequest request = attributes.getRequest();

        // 获取方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        // 获取请求参数
        Object[] args = joinPoint.getArgs();
        String params = Arrays.toString(args);

        // 获取请求信息
        String clientIp = getClientIp(request);
        String serverIp = getServerIp();
        String requestPath = request.getRequestURI();
        String requestMethod = request.getMethod();
        String requestInterface = className + "." + methodName;

        // 打印请求日志
        log.info("========== API请求开始 ==========");
        log.info("请求时间: {}", startTime.format(DATE_TIME_FORMATTER));
        log.info("客户端IP: {}", clientIp);
        log.info("服务器IP: {}", serverIp);
        log.info("请求路径: {}", requestPath);
        log.info("请求方式: {}", requestMethod);
        log.info("请求接口: {}", requestInterface);
        log.info("请求参数: {}", params);

        // 执行目标方法
        Throwable exception = null;
        try {
            // 返回通知
            return joinPoint.proceed();
        } catch (Exception e) {
            // 异常通知
            exception = e;
            throw e;
        } finally {
            // 后置通知
            // 记录结束时间
            LocalDateTime endTime = LocalDateTime.now();
            long endMillis = System.currentTimeMillis();
            long duration = endMillis - startMillis;

            // 打印响应日志
            log.info("响应时间: {}", endTime.format(DATE_TIME_FORMATTER));
            log.info("调用耗时: {} ms", duration);
            if (exception != null) {
                log.error("异常信息: {}", exception.getMessage());
            }
            log.info("========== API请求结束 ==========");
        }
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 获取服务器IP
     */
    private String getServerIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "unknown";
        }
    }
}