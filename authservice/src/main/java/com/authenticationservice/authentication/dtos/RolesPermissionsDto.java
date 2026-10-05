package com.authenticationservice.authentication.dtos;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RolesPermissionsDto {

  private String roleName;
  private Set<String> permissions;
}
