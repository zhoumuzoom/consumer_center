package com.iwhalecloud.customer.mvc.service.impl;

import com.iwhalecloud.customer.mvc.mapper.OperationLogMapper;
import com.iwhalecloud.customer.mvc.pojo.entity.OperationLog;
import com.iwhalecloud.customer.mvc.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 操作日志服务实现
 */
@Slf4j
@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public void log(Long entityId, String entityType, String operationType, String operationDesc) {
        log(entityId, entityType, operationType, operationDesc, null, null);
    }

    @Override
    public void log(Long entityId, String entityType, String operationType, String operationDesc, String beforeData, String afterData) {
        try {
            OperationLog operationLog = new OperationLog();
            operationLog.setEntityId(entityId);
            operationLog.setEntityType(entityType);
            operationLog.setOperationType(operationType);
            operationLog.setOperationDesc(operationDesc);
            operationLog.setBeforeData(beforeData);
            operationLog.setAfterData(afterData);
            operationLog.setOperatorName("系统管理员"); // Demo阶段写死
            operationLog.setOperationTime(LocalDateTime.now());
            operationLogMapper.insert(operationLog);
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }
    }
}
