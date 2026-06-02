package com.authenticationservice.authentication.repositories;

import com.authenticationservice.authentication.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String username);
    List<Users> findUsersByRole_Name(String role);


}
