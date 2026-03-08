package com.authenticationservice.authentication.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddPermissionToRoleDto {
    private Long roleId;
    private Set<Long> permissionIds;
}
