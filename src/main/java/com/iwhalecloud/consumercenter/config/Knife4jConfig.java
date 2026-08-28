package com.iwhalecloud.consumercenter.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置
 */
@Configuration
public class Knife4jConfig {

    private static final String TITLE = "浩鲸科技客户中心模块 API文档";

    private static final String DESCRIPTION = "浩鲸科技-客户中心模块";

    private static final String CONTACT_NAME = "iwhalecloud";

    private static final String VERSION = "v1.0.0";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(TITLE)
                        .description(DESCRIPTION)
                        .contact(new Contact()
                                .name(CONTACT_NAME))
                        .version(VERSION)
                );
    }
}