package org.modsen.servicerating.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.modsen.servicerating.client.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {

    private final TokenProvider tokenProvider;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            String token = tokenProvider.getToken();
            if (token != null) {
                template.header("Authorization", "Bearer " + token);
            }
        };
    }
}

