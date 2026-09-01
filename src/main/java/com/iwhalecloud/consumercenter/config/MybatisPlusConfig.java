package com.iwhalecloud.consumercenter.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.iwhalecloud.consumercenter.interceptor.SqlCostInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * MyBatisPlus分页乐观锁配置
     * @return MybatisPlusInterceptor Bean
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }

    /**
     * MyBatis拦截器
     * @return SqlCostInterceptor Bean
     */
    @Bean
    public SqlCostInterceptor sqlCostInterceptor() {
        return new SqlCostInterceptor();
    }
}