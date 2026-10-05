package com.authenticationservice.authentication.services;

import com.authenticationservice.authentication.dtos.AccountStatus;
import com.authenticationservice.authentication.entities.Roles;
import com.authenticationservice.authentication.entities.Users;
import com.authenticationservice.authentication.exceptions.UserNotFoundException;
import com.authenticationservice.authentication.repositories.RolesRepository;
import com.authenticationservice.authentication.repositories.UsersRepository;
import com.authenticationservice.authentication.utilis.DtoMapper;
import com.authenticationservice.authentication.utilis.HelperMethods;
import com.authenticationservice.security.services.AuthenticationService;
import com.mjengoshareddtos.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UsersRepository usersRepository;
    private final AuthenticationService authenticationService;
    private final HelperMethods helperMethods;
    private final DtoMapper dtoMapper;
    private final RolesRepository rolesRepository;

    public List<UserDto> getAllUsers() {

        return dtoMapper.mapToDto(usersRepository.findAll());
    }

    public String addUser(UserDto userDto) {

        usersRepository.findByEmail(userDto.getEmail())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Email already exists");
                });

        Roles role = helperMethods.getRoleByName(userDto.getRole());


        Users user = Users.builder()
                .email(userDto.getEmail())
                .phoneNumber(userDto.getPhoneNumber())
                .role(role)
                .accountStatus(AccountStatus.ACTIVE)
                .assignedSite(userDto.getAssignedSite())
                .build();

        usersRepository.save(user);

        return "User added successfully with email: " + userDto.getEmail();
    }
    public String updateUserInfo(Long userId, UserDto userDto) {

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setAssignedSite(userDto.getAssignedSite());
        usersRepository.save(user);

        return "User updated successfully";
    }

    public String deleteUser(long userId) {

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        usersRepository.delete(user);
        return "User deleted successfully with email: " + user.getEmail();

    }
    public String toggleAccountStatus(long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setAccountStatus(
                user.getAccountStatus() == AccountStatus.ACTIVE
                        ? AccountStatus.INACTIVE
                        : AccountStatus.ACTIVE
        );

        usersRepository.save(user);

        return "Account status updated to: " + user.getAccountStatus();
    }
    public List<UserDto> getStorekeepers() {

        List<Users> storekeepers = usersRepository.findUsersByRole_Name("STORE_KEEPER");

        return storekeepers.stream()
                .map(dtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public List<String> fetchRoles() {
        return rolesRepository.findAll()
                .stream()
                .map(Roles::getName)
                .collect(Collectors.toList());
    }

    public UserDto getUserByEmail(String email) {

        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return dtoMapper.mapToDto(user);
    }
}
