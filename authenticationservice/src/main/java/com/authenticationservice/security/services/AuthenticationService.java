package com.authenticationservice.security.services;

import com.authenticationservice.authentication.dtos.AccountStatus;
import com.authenticationservice.authentication.entities.Permissions;
import com.authenticationservice.authentication.entities.Roles;
import com.authenticationservice.authentication.entities.Users;
import com.authenticationservice.authentication.exceptions.MissingFieldException;
import com.authenticationservice.authentication.exceptions.UnauthorizedException;
import com.authenticationservice.authentication.exceptions.UserNotFoundException;
import com.authenticationservice.authentication.repositories.PermissionsRepository;
import com.authenticationservice.authentication.repositories.RolesRepository;
import com.authenticationservice.authentication.repositories.UsersRepository;
import com.authenticationservice.authentication.utilis.HelperMethods;
import com.authenticationservice.security.dtos.AuthenticationRequest;
import com.authenticationservice.security.dtos.AuthenticationResponse;
import com.authenticationservice.security.dtos.RegisterRequest;
import com.authenticationservice.security.dtos.RegisterResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsersRepository repository;
    private final HelperMethods helperMethods;

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletRequest servletRequest) {

        String email = request.getEmail();
        String password = request.getPassword().trim();

        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (DisabledException e) {

            log.warn("Authentication attempt for inactive account: {}", email);

            throw new UnauthorizedException("Account is inactive. Please contact administrator.");

        } catch (BadCredentialsException e) {

            log.warn("Invalid credentials for user: {}", email);

            throw new UnauthorizedException("Invalid email or password");
        }

        Users user = repository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        HttpSession session = servletRequest.getSession(true);
        String roleName = user.getRole().getName();
        Set<Permissions> permissions = user.getRole().getPermissions();

        String jwtToken = jwtService.generateAuthenticationToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .role(roleName)
                .permissions(
                        permissions.stream()
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

}