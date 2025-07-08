package com.dashboardTemplate.dashboardTemplate.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("https://dashboardtemplate.duckdns.org"))
                .info(new Info()
                        .title("API 문서 제목")
                        .description("API 문서 설명")
                        .version("v1.0.0"));
    }
}
