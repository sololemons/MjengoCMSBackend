package com.authenticationservice.authentication.repositories;

import com.authenticationservice.authentication.entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long> {
    Optional<Roles> findByNameIgnoreCase(String name);
}
