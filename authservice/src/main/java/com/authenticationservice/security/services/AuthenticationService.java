package com.authenticationservice.security.services;

import com.authenticationservice.authentication.dtos.AccountStatus;
import com.authenticationservice.authentication.entities.RefreshToken;
import com.authenticationservice.authentication.entities.Permissions;
import com.authenticationservice.authentication.entities.Roles;
import com.authenticationservice.authentication.entities.Users;
import com.authenticationservice.authentication.exceptions.MissingFieldException;
import com.authenticationservice.authentication.exceptions.UnauthorizedException;
import com.authenticationservice.authentication.exceptions.UserNotFoundException;
import com.authenticationservice.authentication.repositories.RefreshTokenRepository;
import com.authenticationservice.authentication.repositories.UsersRepository;
import com.authenticationservice.authentication.utilis.HelperMethods;
import com.authenticationservice.security.dtos.AuthenticationRequest;
import com.authenticationservice.security.dtos.AuthenticationResponse;
import com.authenticationservice.security.dtos.RegisterRequest;
import com.authenticationservice.security.dtos.RegisterResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsersRepository repository;
    private final HelperMethods helperMethods;
    private final RefreshTokenRepository refreshTokenRepository;


    @Transactional
    public AuthenticationResponse authenticate(
            AuthenticationRequest request,
            HttpServletResponse response
    ) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword().trim()
                    )
            );
        } catch (DisabledException ex) {
            throw new UnauthorizedException(
                    "Account is inactive. Please contact administrator."
            );
        } catch (BadCredentialsException ex) {
            throw new UnauthorizedException(
                    "Invalid email or password"
            );
        }

        Users user = repository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found: " + request.getEmail()
                        )
                );

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        helperMethods.revokeAllUserTokens(user);
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .user(user)
                        .token(refreshToken)
                        .expired(false)
                        .revoked(false)
                        .build()
        );

        ResponseCookie refreshCookie = ResponseCookie.from(
                        "refresh_token",
                        refreshToken
                )
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/auth/refresh")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                refreshCookie.toString()
        );

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .role(user.getRole().getName())
                .permissions(
                        user.getRole()
                                .getPermissions()
                                .stream()
                                .map(Permissions::getPermissionName)
                                .toList()
                )
                .build();
    }

    public RegisterResponse register(RegisterRequest request) {

        String email = request.getEmail();
        String password = request.getPassword().trim();
        String confirmPassword = request.getConfirmPassword().trim();

        if (!password.equals(confirmPassword)) {
            throw new MissingFieldException("Passwords do not match!");
        }

        Optional<Users> optionalUser = repository.findByEmail(email);

        Users user;

        if (repository.count() == 0) {

            Roles superAdminRole = helperMethods.getOrCreateSuperAdminRole();

            user = Users.builder()
                    .email(email)
                    .phoneNumber(request.getPhoneNumber())
                    .role(superAdminRole)
                    .accountStatus(AccountStatus.ACTIVE)
                    .password(passwordEncoder.encode(password))
                    .build();

            repository.save(user);
        } else {

            if (optionalUser.isEmpty()) {
                throw new MissingFieldException(
                        "User must be created by admin before registration"
                );
            }

            user = optionalUser.get();
            if (!request.getPassword().equals(confirmPassword)) {
                throw new MissingFieldException("Passwords do not match");
            }

            if (user.getPassword() != null) {
                throw new MissingFieldException("User already registered");
            }
            user.setPassword(passwordEncoder.encode(password));


            repository.save(user);
        }

        String jwtToken = jwtService.generateRegistrationToken(user);

        return RegisterResponse.builder()
                .token(jwtToken)
                .role(user.getRole().getName())
                .permissions(
                        user.getRole()
                                .getPermissions()
                                .stream()
                                .map(Permissions::getPermissionName)
                                .toList()
                )
                .build();
    }
    public AuthenticationResponse refresh(String refreshToken) {
        System.out.println("TOKEN RECEIVED FROM CLIENT: " + refreshToken);
        String username = jwtService.extractUserName(refreshToken);
        Users user = repository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (storedToken.isExpired() || storedToken.isRevoked()) {
            throw new UnauthorizedException("Refresh token no longer valid");
        }

        helperMethods.revokeAllUserTokens(user);
        refreshTokenRepository.save(storedToken);

        String newAccessToken = jwtService.generateAccessToken(user);
        log.info("generated new access token for user: {}", newAccessToken);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        RefreshToken newTokenEntity = new RefreshToken();
        newTokenEntity.setUser(user);
        newTokenEntity.setToken(newRefreshToken);
        newTokenEntity.setExpired(false);
        newTokenEntity.setRevoked(false);
        refreshTokenRepository.save(newTokenEntity);
      log.info("generated new refresh token for user: {}", newRefreshToken);
        return AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .role(user.getRole().getName())
                .permissions(user.getRole().getPermissions().stream()
                        .map(Permissions::getPermissionName).toList())
                .build();
    }

}