package org.modsen.service.driver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import java.util.List;
import java.util.stream.Stream;

@Configuration
@EnableMethodSecurity
public class SecurityConf {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        var authorities = new JwtGrantedAuthoritiesConverter();
        converter.setPrincipalClaimName("preffered_username");
        converter.setJwtGrantedAuthoritiesConverter(
                jwt -> {
                    var auth = authorities.convert(jwt);
                    var roles = (List<String>) jwt.getClaimAsMap("realm_access").get("roles");
                    return Stream.concat(auth.stream(), roles.stream()
                                    .filter(role -> role.startsWith("ROLE_"))
                                    .map(role -> new SimpleGrantedAuthority(role))
                                    .map(GrantedAuthority.class::cast))
                            .toList();
                }
        );

        return converter;
    }
}
