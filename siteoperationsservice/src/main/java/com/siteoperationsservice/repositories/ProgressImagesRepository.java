package com.siteoperationsservice.repositories;

import com.siteoperationsservice.entities.ProjectProgressImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgressImagesRepository extends JpaRepository<ProjectProgressImage, Long> {
    Optional<ProjectProgressImage> findByIdAndProject_Id(Long imageId, String constructionId);
}
