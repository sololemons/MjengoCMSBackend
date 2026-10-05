package com.authenticationservice.authentication.controllers;

import com.authenticationservice.authentication.services.UserService;
import com.mjengoshareddtos.UserDto;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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
    @GetMapping("/fetch/roles")
    public ResponseEntity<List<String>> fetchRoles() {
        return ResponseEntity.ok(
                userService.fetchRoles()
        );
    }
    @PreAuthorize("hasAuthority('CREATE_PERMISSIONS')")
    @PatchMapping("/toggle/account/status")
    public ResponseEntity<String> toggleStatus(@RequestParam long userId) {
        return ResponseEntity.ok(userService.toggleAccountStatus(userId));
    }

    @GetMapping("/get/storekeepers")
    public ResponseEntity<List<UserDto>> getStorekeepers() {
        log.info("Fetching storekeepers from UserController");
        return ResponseEntity.ok(userService.getStorekeepers());

}
    @GetMapping("/me")
    public ResponseEntity<UserDto> getMyDetails(Principal principal) {

        String email = principal.getName();

        UserDto currentUser = userService.getUserByEmail(email);

        return ResponseEntity.ok(currentUser);
    }
}
