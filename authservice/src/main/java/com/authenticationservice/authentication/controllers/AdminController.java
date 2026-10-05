package com.authenticationservice.authentication.controllers;

import com.authenticationservice.authentication.dtos.AddPermissionToRoleDto;
import com.authenticationservice.authentication.dtos.PermissionsCreatorDto;
import com.authenticationservice.authentication.dtos.PermissionsDto;
import com.authenticationservice.authentication.dtos.RoleCreatorDto;
import com.authenticationservice.authentication.dtos.RolesPermissionsDto;
import com.authenticationservice.authentication.entities.Permissions;
import com.authenticationservice.authentication.services.AdminService;
import java.util.List;
import java.util.Set;
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
    @PostMapping("/add/permissions")
    public ResponseEntity<String> addPermissions(@RequestBody AddPermissionToRoleDto addPermissionToRoleDto) {
        return ResponseEntity.ok(adminService.assignPermissionsToRole(addPermissionToRoleDto));
    }
    @DeleteMapping("/delete/role")
    public ResponseEntity<String> deleteRole(@RequestParam String roleName) {
        return ResponseEntity.ok(adminService.deleteRole(roleName));
    }
    @DeleteMapping("/delete/permission/from/role")
    public ResponseEntity<String> deletePermissionFromRole(@RequestBody AddPermissionToRoleDto addPermissionToRoleDto) {
        return ResponseEntity.ok(adminService.removePermissionsFromRole(addPermissionToRoleDto));
    }
    @GetMapping("/fetch/roles/permissions")
    public ResponseEntity<List<RolesPermissionsDto>> fetchRolesPermissions() {
        return ResponseEntity.ok(adminService.fetchRolesPermissions());
    }
    @GetMapping("/fetch/permissions")
    public ResponseEntity<Set<PermissionsDto>> fetchPermissions() {
        return ResponseEntity.ok(adminService.fetchPermissions());
    }

}
