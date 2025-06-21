package com.dashboardTemplate.dashboardTemplate.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API 문서 제목")
                        .description("API 문서 설명")
                        .version("v1.0.0"));
    }
}
