package org.modsen.serviceride.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import java.util.List;

public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(
                        List.of(new Server().url("http://localhost:8081"))
                )
                .info(new Info().title("Service-ride rest api"));
    }
}
