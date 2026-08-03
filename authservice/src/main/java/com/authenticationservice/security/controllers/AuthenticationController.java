package com.authenticationservice.security.controllers;

import com.authenticationservice.authentication.exceptions.UnauthorizedException;
import com.authenticationservice.authentication.exceptions.UserNotFoundException;
import com.authenticationservice.authentication.repositories.RefreshTokenRepository;
import com.authenticationservice.authentication.repositories.UsersRepository;
import com.authenticationservice.security.dtos.AuthenticationRequest;
import com.authenticationservice.security.dtos.AuthenticationResponse;
import com.authenticationservice.security.dtos.RegisterRequest;
import com.authenticationservice.security.dtos.RegisterResponse;
import com.authenticationservice.security.services.AuthenticationService;
import com.authenticationservice.security.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    private final JwtService jwtService;
    private final UsersRepository repository;

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
        return ResponseEntity.ok(service.authenticate(request, servletResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
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

        AuthenticationResponse authResponse = service.refresh(refreshToken);



        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", authResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/auth/refresh")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        AuthenticationResponse clientResponse = AuthenticationResponse.builder()
                .accessToken(authResponse.getAccessToken())
                .role(authResponse.getRole())
                .permissions(authResponse.getPermissions())
                .build();

        return ResponseEntity.ok(clientResponse);
    }


}