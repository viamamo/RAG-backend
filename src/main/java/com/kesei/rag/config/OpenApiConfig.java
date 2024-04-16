package com.kesei.rag.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author viamamo
 */
@Configuration
@EnableKnife4j
public class OpenApiConfig {
    @Bean
    public OpenAPI springOpenApi() {
        return new OpenAPI().info(new Info()
                .title("SpringDoc API Test")
                .description("SpringDoc Simple Application Test")
                .version("0.0.1"));
    }
}