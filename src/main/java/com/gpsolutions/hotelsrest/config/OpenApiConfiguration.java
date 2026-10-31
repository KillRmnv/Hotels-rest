package com.gpsolutions.hotelsrest.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class OpenApiConfiguration {

    private static final String DESCRIPTION = """
			Provides simple REST API to perform CR operations on hotels and receive statistics about it.
			""";

    @Bean
    OpenAPI financialTransactionsApi() {
        return new OpenAPI().info(new Info().title("Hotel REST API").version("v1").description(DESCRIPTION)
                );
    }

}