package com.authenticationservice.authentication.repositories;

import com.authenticationservice.authentication.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);


    @Query("""
            select r from RefreshToken r inner join r.user u
            where u.userId = :userId and (r.expired = false and r.revoked = false)
            """)
    List<RefreshToken> findAllValidTokensByUser(Long userId);
}