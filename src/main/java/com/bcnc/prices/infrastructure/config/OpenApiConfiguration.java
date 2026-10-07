package com.bcnc.prices.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI pricesOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Prices Service API")
                .description("Consulta del precio final aplicable a un producto de una cadena en una fecha")
                .version("v1"));
    }
}
