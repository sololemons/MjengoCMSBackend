package com.storekeeperservice.repositories;

import com.storekeeperservice.entities.Materials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialsRepository extends JpaRepository<Materials, Long> {
    Optional<Materials> findByMaterialName(String materialName);

    @Query("SELECT m FROM Materials m WHERE m.quantity < m.minThreshold")
    List<Materials> findMaterialsBelowThreshold();
}
