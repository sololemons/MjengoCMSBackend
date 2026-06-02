package com.apigatewayservice.jwtvalidator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;

@Configuration
public class JwtDecoderConfig {
    @Value("${secret.key}")
    private String secretKey;



    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(secretKey);
        SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");
        return NimbusReactiveJwtDecoder.withSecretKey(key).build();
    }



    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> permissions = jwt.getClaim("permissions");
            if (permissions == null) {
                permissions = new java.util.ArrayList<>();
            }

            String role = jwt.getClaim("role");

            List<SimpleGrantedAuthority> authorities = new java.util.ArrayList<>(
                    permissions.stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList()
            );

            // 4. Add the ROLE_ prefix to the role and add to list
            if (role != null && !role.isEmpty()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }

            // 5. Return as a Flux (Safe even if the list is empty)
            return Flux.fromIterable(authorities);
        });

        return converter;
    }
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         ReactiveJwtDecoder jwtDecoder) {

        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth/register").permitAll()
                        .pathMatchers("/auth/authenticate").permitAll()
                     //   .pathMatchers("/auth/user/get/storekeepers").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtDecoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );

        return http.build();
    }
}