package org.modsen.serviceride.config;

import feign.RequestInterceptor;
import feign.Retryer;
import lombok.RequiredArgsConstructor;
import org.modsen.serviceride.client.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignClientConfig {

    private final TokenProvider tokenProvider;

    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(1000, 2000, 3);
    }

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
