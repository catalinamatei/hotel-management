package com.hotel_management.booking_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
  @Bean
  public OpenAPI hotelManagementOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Hotel Management API")
                .description("API for managing hotel rooms")
                .version("v1.0"));
  }
}
