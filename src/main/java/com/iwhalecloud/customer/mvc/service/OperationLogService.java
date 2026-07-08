package com.iwhalecloud.customer.mvc.service;

/**
 * 操作日志服务
 */
public interface OperationLogService {

    /**
     * 记录操作日志
     *
     * @param entityId      实体ID
     * @param entityType    实体类型
     * @param operationType 操作类型
     * @param operationDesc 操作描述
     */
    void log(Long entityId, String entityType, String operationType, String operationDesc);

    /**
     * 记录操作日志（带前后数据）
     */
    void log(Long entityId, String entityType, String operationType, String operationDesc, String beforeData, String afterData);
}
