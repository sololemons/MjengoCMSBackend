package com.apigatewayservice.jwtvalidator;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class UserEmailHeaderFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        return ReactiveSecurityContextHolder.getContext()
                .filter(context -> context.getAuthentication() != null && context.getAuthentication().getPrincipal() instanceof Jwt)
                .map(context -> (Jwt) context.getAuthentication().getPrincipal())
                .flatMap(jwt -> {

                    String email = jwt.getSubject();

                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(r -> r.header("X-User-Email", email))
                            .build();

                    return chain.filter(mutatedExchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }

}