package com.iwhalecloud.consumercenter.mvc.service;

/**
 * 模拟外部数据服务
 * 模拟集团139要客系统、中资出海系统等外部数据源
 */
public interface MockDataService {

    /**
     * 模拟从集团139要客系统获取要客清单
     */
    void syncFromGroup139();

    /**
     * 模拟从中资出海系统获取企业数据
     */
    void syncFromOverseaSystem();

    /**
     * 模拟集团上报接口
     */
    String mockGroupReport(String reportData);
}
