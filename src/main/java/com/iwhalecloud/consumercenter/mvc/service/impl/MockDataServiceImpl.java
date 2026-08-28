package com.iwhalecloud.consumercenter.mvc.service.impl;

import com.iwhalecloud.consumercenter.mvc.service.MockDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 模拟外部数据服务实现
 */
@Slf4j
@Service
public class MockDataServiceImpl implements MockDataService {

    @Override
    public void syncFromGroup139() {
        log.info("[模拟] 从集团139要客系统同步数据...");
        // 模拟同步逻辑，实际项目中这里会调用集团接口
        try {
            Thread.sleep(1000); // 模拟网络延迟
            log.info("[模拟] 集团139系统数据同步完成");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void syncFromOverseaSystem() {
        log.info("[模拟] 从中资出海系统同步数据...");
        try {
            Thread.sleep(1000);
            log.info("[模拟] 中资出海系统数据同步完成");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public String mockGroupReport(String reportData) {
        log.info("[模拟] 上报数据到集团系统，数据长度: {}", reportData != null ? reportData.length() : 0);
        // 模拟上报成功
        return "{\"status\":\"success\",\"message\":\"数据上报成功\"}";
    }
}
