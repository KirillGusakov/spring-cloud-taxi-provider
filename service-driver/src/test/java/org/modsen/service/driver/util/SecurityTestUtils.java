package org.modsen.service.driver.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.Collections;

public class SecurityTestUtils {
    public static void setUpSecurityContextWithRole(String role) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "user",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static String obtainAccessToken() {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8082")
                .build();

        try {
            String response = webClient.post()
                    .uri("/realms/taxi/protocol/openid-connect/token")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .bodyValue("client_id=taxi-app" +
                               "&client_secret=urRF4OEUsRtm5HYKTJBeE3B03no8Zibp" +
                               "&username=kirill" +
                               "&password=password" +
                               "&grant_type=password")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            String accessToken = response.split("\"access_token\":\"")[1].split("\"")[0];
            return accessToken;

        } catch (WebClientResponseException e) {
            System.err.println("Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException("Failed to obtain access token", e);
        }
    }
}

