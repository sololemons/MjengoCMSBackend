package com.authenticationservice.security.controllers;

import com.authenticationservice.authentication.exceptions.UnauthorizedException;
import com.authenticationservice.security.dtos.AuthenticationRequest;
import com.authenticationservice.security.dtos.AuthenticationResponse;
import com.authenticationservice.security.dtos.RegisterRequest;
import com.authenticationservice.security.dtos.RegisterResponse;
import com.authenticationservice.security.services.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request,
            HttpServletResponse servletResponse
    ) {
        return ResponseEntity.ok(service.authenticate(request,servletResponse));
    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(HttpServletRequest request) {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (refreshToken == null) {
            throw new UnauthorizedException("Refresh token is missing");
        }

        return ResponseEntity.ok(service.refresh(refreshToken));
    }
}