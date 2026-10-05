package com.authenticationservice.authentication.repositories;

import com.authenticationservice.authentication.entities.Permissions;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionsRepository extends JpaRepository<Permissions, Long> {

  Optional<Permissions> findByPermissionNameIgnoreCase(String name);

}
