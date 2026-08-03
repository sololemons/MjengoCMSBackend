package com.siteoperationsservice.repositories;

import com.siteoperationsservice.entities.ProjectObjective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectObjectiveRepository extends JpaRepository<ProjectObjective, Long> {

    Optional<ProjectObjective> findByIdAndConstructionProject_Id(Long objectiveId, String constructionId);
}
