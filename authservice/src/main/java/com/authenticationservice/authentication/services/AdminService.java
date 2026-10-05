package com.authenticationservice.authentication.services;

import com.authenticationservice.authentication.dtos.AddPermissionToRoleDto;
import com.authenticationservice.authentication.dtos.PermissionsCreatorDto;
import com.authenticationservice.authentication.dtos.PermissionsDto;
import com.authenticationservice.authentication.dtos.RoleCreatorDto;
import com.authenticationservice.authentication.dtos.RolesPermissionsDto;
import com.authenticationservice.authentication.entities.Permissions;
import com.authenticationservice.authentication.entities.Roles;
import com.authenticationservice.authentication.exceptions.UserNotFoundException;
import com.authenticationservice.authentication.repositories.PermissionsRepository;
import com.authenticationservice.authentication.repositories.RolesRepository;
import java.util.HashSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

  private final RolesRepository rolesRepository;
  private final PermissionsRepository permissionsRepository;

  @Transactional
  public String createRoles(RoleCreatorDto roleCreatorDto) {
    Roles roles = new Roles();
    roles.setName(roleCreatorDto.getRoleName().toUpperCase());
    rolesRepository.save(roles);
    return "Role_" + roleCreatorDto.getRoleName().toUpperCase() + "_created successfully";

  }

  @Transactional
  public String createPermissions(PermissionsCreatorDto permissionsCreatorDto) {
    List<Permissions> permissionsList = permissionsCreatorDto.getPermissionName().stream()
        .map(name -> {
          Permissions permission = new Permissions();
          permission.setPermissionName(name.trim());
          return permission;
        })
        .collect(Collectors.toList());

    permissionsRepository.saveAll(permissionsList);
    return "Permissions_" + permissionsList + "_created successfully";
  }

  public String assignPermissionsToRole(AddPermissionToRoleDto dto) {
    Roles role = rolesRepository.findByNameIgnoreCase(dto.getRoleName())
        .orElseThrow(() -> new UserNotFoundException("Role not found"));

    Set<Permissions> permissions = dto.getPermissionNames().stream()
        .map(permissionName -> permissionsRepository.findByPermissionNameIgnoreCase(permissionName)
            .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionName)))
        .collect(Collectors.toSet());

    role.setPermissions(permissions);
    rolesRepository.save(role);

    return "Permissions updated successfully for role " + role.getName();
  }

  public String removePermissionsFromRole(AddPermissionToRoleDto dto) {
    Roles role = rolesRepository.findByNameIgnoreCase(dto.getRoleName())
        .orElseThrow(() -> new RuntimeException("Role not found"));

    if (role.getPermissions() == null || role.getPermissions().isEmpty()) {
      return "No permissions to remove from role " + role.getName();
    }

    Set<Permissions> permissionsToRemove = dto.getPermissionNames().stream()
        .map(permissionName -> permissionsRepository.findByPermissionNameIgnoreCase(permissionName)
            .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionName)))
        .collect(Collectors.toSet());

    role.getPermissions().removeAll(permissionsToRemove);

    rolesRepository.save(role);

    return "Permissions removed successfully from role " + role.getName();
  }

  public List<RolesPermissionsDto> fetchRolesPermissions() {
    List<Roles> rolesList = rolesRepository.findAll();

    return rolesList.stream()
        .map(role -> {
          Set<String> permissionNames = role.getPermissions().stream()
              .map(Permissions::getPermissionName)
              .collect(Collectors.toSet());

          return new RolesPermissionsDto(role.getName(), permissionNames);
        })
        .collect(Collectors.toList());
  }

  public Set<PermissionsDto> fetchPermissions() {
    List<Permissions> permissionsList = permissionsRepository.findAll();

    return permissionsList.stream()
        .map(permission -> new PermissionsDto(permission.getPermissionName()))
        .collect(Collectors.toSet());
  }

  public String deleteRole(String roleName) {
    Roles role = rolesRepository.findByNameIgnoreCase(roleName)
        .orElseThrow(() -> new UserNotFoundException("Role not found"));

    rolesRepository.delete(role);

    return "Role_" + roleName + "_deleted successfully";
  }
}
