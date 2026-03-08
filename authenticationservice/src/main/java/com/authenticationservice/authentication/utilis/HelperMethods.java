package com.authenticationservice.authentication.utilis;

import com.authenticationservice.authentication.entities.Permissions;
import com.authenticationservice.authentication.entities.Roles;
import com.authenticationservice.authentication.exceptions.UserNotFoundException;
import com.authenticationservice.authentication.repositories.PermissionsRepository;
import com.authenticationservice.authentication.repositories.RolesRepository;
import com.authenticationservice.authentication.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@Service
public class HelperMethods {
    private final RolesRepository rolesRepository;
    private final PermissionsRepository permissionsRepository;

    public Roles getRoleByName(String roleName) {
        return rolesRepository.findByNameIgnoreCase(roleName.trim())
                .orElseThrow(() -> new UserNotFoundException("Role not found: " + roleName));
    }
    public Roles getOrCreateSuperAdminRole() {

        return rolesRepository.findByNameIgnoreCase("SUPER_ADMIN")
                .orElseGet(() -> {

                    Roles role = new Roles();
                    role.setName("SUPER_ADMIN");

                    List<Permissions> permissions =
                            permissionsRepository.findAll();

                    role.setPermissions(new HashSet<>(permissions));

                    return rolesRepository.save(role);
                });
    }
}
