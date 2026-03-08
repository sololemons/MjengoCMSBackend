package com.authenticationservice.authentication.controllers;

import com.authenticationservice.authentication.dtos.UserDto;
import com.authenticationservice.authentication.repositories.UsersRepository;
import com.authenticationservice.authentication.services.UserService;
import com.authenticationservice.authentication.utilis.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/get/all/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }

    @PostMapping("/add/users")
    public ResponseEntity<String> addUser(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(
                userService.addUser(userDto)
        );
    }

    @PutMapping("/update/user/info")
    public ResponseEntity<String> updateUser(
            @RequestParam long userId,
            @RequestBody UserDto userDto) {

        return ResponseEntity.ok(
                userService.updateUserInfo(userId, userDto)
        );
    }

    @DeleteMapping("/delete/user")
    public ResponseEntity<String> deleteUser(@RequestParam long userId) {
        return ResponseEntity.ok(
                userService.deleteUser(userId)
        );

    }
    @PreAuthorize("hasAuthority('CREATE_PERMISSIONS')")
    @PatchMapping("/toggle/account/status")
    public ResponseEntity<String> toggleStatus(@RequestParam long userId) {
        return ResponseEntity.ok(userService.toggleAccountStatus(userId));
    }

}
