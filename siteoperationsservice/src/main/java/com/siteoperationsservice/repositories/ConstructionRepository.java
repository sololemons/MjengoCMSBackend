package com.siteoperationsservice.repositories;

import com.siteoperationsservice.entities.ConstructionProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ConstructionRepository extends JpaRepository<ConstructionProject, String> {
}
