package com.apigatewayservice.config;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

  @Override
  public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

    String errorMessage = "Unauthorized";
    if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("expired")) {
      errorMessage = "token expired";
    }

    String jsonBody = String.format("{\"error\": \"Unauthorized\", \"message\": \"%s\"}", errorMessage);

    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(jsonBody.getBytes(StandardCharsets.UTF_8));
    return exchange.getResponse().writeWith(Mono.just(buffer));
  }
}