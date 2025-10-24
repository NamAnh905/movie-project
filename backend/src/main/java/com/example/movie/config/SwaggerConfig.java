package com.example.movie.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * springdoc-openapi (swagger-ui) cấu hình:
 * - Tiêu đề, mô tả.
 * - Bảo mật Bearer JWT cho thử API trực tiếp.
 * Truy cập UI: /swagger-ui/index.html
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI movieAdminOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Movie Admin API")
                        .version("v1.0")
                        .description("REST API cho web quản lý phim"))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components().addSecuritySchemes(
                        SECURITY_SCHEME_NAME,
                        new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ));
    }

    @Bean
    public GroupedOpenApi apiGroup() {
        return GroupedOpenApi.builder()
                .group("movie-admin")
                .pathsToMatch("/api/**")
                .build();
    }
}
