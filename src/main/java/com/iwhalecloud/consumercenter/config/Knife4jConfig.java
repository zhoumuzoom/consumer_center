package com.iwhalecloud.consumercenter.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Knife4j配置
 */
@Configuration
public class Knife4jConfig {

    private static final String TITLE = "CMI中移国际要客平台-客户中心模块 API文档";

    private static final String DESCRIPTION = "CMI中移国际要客平台-客户中心模块 。除登录与刷新令牌接口外，其余接口均需在token请求头携带accessToken";

    private static final String CONTACT_NAME = "iwhalecloud";

    private static final String VERSION = "v1.0.0";

    /**
     * 认证方案名称
     */
    private static final String SECURITY_SCHEME_NAME = "token";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(TITLE)
                        .description(DESCRIPTION)
                        .contact(new Contact()
                                .name(CONTACT_NAME))
                        .version(VERSION)
                )
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(SECURITY_SCHEME_NAME)
                                .description("accessToken，通过POST /api/auth/login获取")))
                .security(List.of(new SecurityRequirement().addList(SECURITY_SCHEME_NAME)));
    }
}
