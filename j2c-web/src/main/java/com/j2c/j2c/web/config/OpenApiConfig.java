package com.j2c.j2c.web.config;

import com.j2c.j2c.web.output.ErrorResponse;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class OpenApiConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        // redirect to swagger documentation
        registry.addViewController("/").setViewName("forward:/swagger-ui.html");
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "JWT",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .in(SecurityScheme.In.HEADER)
                                                .name(HttpHeaders.AUTHORIZATION)
                                )
                                .addResponses(
                                        "401",
                                        new ApiResponse()
                                                .description("When authentication is required and no token is supplied, or the supplied Authorization header is " +
                                                        "otherwise invalid (no 'Bearer' prefix, wrong signature, expired or malformed token)")
                                                .addHeaderObject(
                                                        HttpHeaders.WWW_AUTHENTICATE,
                                                        new Header()
                                                                .description("Describes the reason for which authentication failed")
                                                                .schema(
                                                                        new Schema<>()
                                                                                .example(
                                                                                        "Bearer realm=\"j2c\", " +
                                                                                                "error=\"invalid_token\", " +
                                                                                                "error_description=\"Token expired.\""
                                                                                )
                                                                )
                                                )
                                )
                                .addResponses(
                                        "403",
                                        new ApiResponse()
                                                .description("When the supplied token has insufficient privileges")
                                                .addHeaderObject(
                                                        HttpHeaders.WWW_AUTHENTICATE,
                                                        new Header()
                                                                .description("Describes the reason for which authorization failed")
                                                                .schema(
                                                                        new Schema<>()
                                                                                .example(
                                                                                        "Bearer realm=\"j2c\", " +
                                                                                                "error=\"insufficient_scope\"," +
                                                                                                "error_description=\"The request requires higher privileges than provided by the access token.\""
                                                                                )
                                                                )
                                                )
                                )
                                .addResponses(
                                        "404",
                                        new ApiResponse()
                                                .description("When no resource exists for any supplied id")
                                                .content(
                                                        new Content()
                                                                .addMediaType(
                                                                        MediaType.APPLICATION_JSON_VALUE,
                                                                        new io.swagger.v3.oas.models.media.MediaType()
                                                                                .example(
                                                                                        ErrorResponse.builder()
                                                                                                .status(HttpStatus.NOT_FOUND)
                                                                                                .build()
                                                                                )
                                                                )
                                                )
                                )
                                .addResponses(
                                        "400",
                                        new ApiResponse()
                                                .description("When the request is malformed or any user input is invalid")
                                                .content(
                                                        new Content()
                                                                .addMediaType(
                                                                        MediaType.APPLICATION_JSON_VALUE,
                                                                        new io.swagger.v3.oas.models.media.MediaType()
                                                                                .example(
                                                                                        ErrorResponse.builder()
                                                                                                .status(HttpStatus.BAD_REQUEST)
                                                                                                .build()
                                                                                )
                                                                )
                                                )
                                )
                                .addResponses(
                                        "409",
                                        new ApiResponse()
                                                .description("When a unique resource already exists, or a resource with a supplied unique value already exists")
                                                .content(
                                                        new Content()
                                                                .addMediaType(
                                                                        MediaType.APPLICATION_JSON_VALUE,
                                                                        new io.swagger.v3.oas.models.media.MediaType()
                                                                                .example(
                                                                                        ErrorResponse.builder()
                                                                                                .status(HttpStatus.CONFLICT)
                                                                                                .build()
                                                                                )
                                                                )
                                                )
                                )
                                .addResponses(
                                        "422",
                                        new ApiResponse()
                                                .description("When the requested operation does not conform to some domain rule")
                                                .content(
                                                        new Content()
                                                                .addMediaType(
                                                                        MediaType.APPLICATION_JSON_VALUE,
                                                                        new io.swagger.v3.oas.models.media.MediaType()
                                                                                .example(
                                                                                        ErrorResponse.builder()
                                                                                                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                                                                                                .build()
                                                                                )
                                                                )
                                                )
                                )
                );
    }

}
