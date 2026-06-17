package com.authenticationservice.authentication.controllers;

import com.authenticationservice.authentication.dtos.AddPermissionToRoleDto;
import com.authenticationservice.authentication.dtos.PermissionsCreatorDto;
import com.authenticationservice.authentication.dtos.RoleCreatorDto;
import com.authenticationservice.authentication.entities.Permissions;
import com.authenticationservice.authentication.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth/user")
@RestController
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    @PostMapping("/create/roles")
    public ResponseEntity<String> createRoles(@RequestBody RoleCreatorDto roleCreatorDto) {
        return ResponseEntity.ok(adminService.createRoles(roleCreatorDto));
    }
    @PostMapping("/create/permissions")
    public ResponseEntity<String> createPermissions(@RequestBody PermissionsCreatorDto permissionsCreatorDto) {
        return ResponseEntity.ok(adminService.createPermissions(permissionsCreatorDto));
    }
    @PreAuthorize("hasAuthority('CREATE_PERMISSIONS')")
    @PostMapping("/add/permissions")
    public ResponseEntity<String> addPermissions(@RequestBody AddPermissionToRoleDto addPermissionToRoleDto) {
        return ResponseEntity.ok(adminService.assignPermissionsToRole(addPermissionToRoleDto));
    }
    @DeleteMapping("/delete/permission/from/role")
    public ResponseEntity<String> deletePermissionFromRole(@RequestBody AddPermissionToRoleDto addPermissionToRoleDto) {
        return ResponseEntity.ok(adminService.removePermissionsFromRole(addPermissionToRoleDto));
    }

}
