package com.authenticationservice.authentication.utilis;

import com.authenticationservice.authentication.dtos.AccountStatus;
import com.authenticationservice.authentication.entities.Users;
import com.authenticationservice.authentication.exceptions.UserNotFoundException;
import com.authenticationservice.authentication.repositories.RolesRepository;
import com.authenticationservice.authentication.repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ServiceAccountSetup implements CommandLineRunner {

    private final UsersRepository userRepository;
    private final RolesRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String @NonNull ... args) {
        String serviceEmail = "mjengosystem@gmail.com";

        if (userRepository.findByEmail(serviceEmail).isEmpty()) {
            String rawSecret = UUID.randomUUID().toString().replace("-", "");

            var systemRole = roleRepository.findByNameIgnoreCase("SYSTEM")
                    .orElseThrow(() -> new UserNotFoundException("Role 'SYSTEM' not found in DB Create it first."));

            Users serviceAccount = Users.builder()
                    .email(serviceEmail)
                    .password(passwordEncoder.encode(rawSecret))
                    .accountStatus(AccountStatus.ACTIVE)
                    .phoneNumber("0000000000")
                    .role(systemRole)
                    .build();

            userRepository.save(serviceAccount);

            System.out.println("\n########################################################");
            System.out.println("CREATED NEW SERVICE ACCOUNT");
            System.out.println("ID: " + serviceEmail);
            System.out.println("SECRET: " + rawSecret);
            System.out.println("########################################################\n");
        }
    }
}