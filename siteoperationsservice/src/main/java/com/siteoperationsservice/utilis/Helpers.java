package com.siteoperationsservice.utilis;

import com.siteoperationsservice.dtos.ConstructionDto;
import com.siteoperationsservice.entities.ConstructionProject;
import com.siteoperationsservice.entities.ProjectObjective;
import com.siteoperationsservice.entities.ProjectProgressImage;
import com.siteoperationsservice.exceptions.ResourceNotFoundException;
import com.siteoperationsservice.repositories.ConstructionRepository;
import com.siteoperationsservice.repositories.ProjectObjectiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class Helpers {

  private final ConstructionRepository constructionRepository;
  private final GeocodingService geocodingService;

  public ConstructionProject createConstruction(ConstructionDto constructionDto) {

    ConstructionProject project = new ConstructionProject();
    project.setConstructionName(constructionDto.getConstructionName());
    project.setDescription(constructionDto.getDescription());
    project.setStartDate(constructionDto.getStartDate());
    project.setEstimatedEndDate(constructionDto.getEstimatedEndDate());
    project.setAssignedEngineerEmails(constructionDto.getAssignedEngineerEmail());

    Double lat =
        constructionDto.getLatitude() != null ? Double.valueOf(constructionDto.getLatitude())
            : null;
    Double lon =
        constructionDto.getLongitude() != null ? Double.valueOf(constructionDto.getLongitude())
            : null;

    project.setLatitude(lat);
    project.setLongitude(lon);

    String locationName = geocodingService.getLocationName(lat, lon);
    project.setLocationName(locationName);

    if (constructionDto.getProjectObjectives() != null) {
      List<ProjectObjective> objectives = constructionDto.getProjectObjectives().
          stream().
          map(
              dto -> {
                ProjectObjective objective = new ProjectObjective();
                objective.setTitle(dto.getTitle());
                objective.setDescription(dto.getDescription());
                objective.setEstimatedDurationDays(dto.getEstimatedDurationDays());
                objective.setCompleted(dto.isCompleted());
                objective.setDateCompleted(dto.getDateCompleted());
                objective.setConstructionProject(project);
                return objective;
              }).toList();

      project.setObjectives(objectives);
    }

    if (constructionDto.getProgressImages() != null) {
      List<ProjectProgressImage> images = constructionDto.getProgressImages().
          stream().
          map(
              dto -> {
                ProjectProgressImage image = new ProjectProgressImage();
                image.setImageUrl(dto.getImageUrl());
                image.setLatitude(dto.getLatitude());
                image.setLongitude(dto.getLongitude());
                image.setDescription(dto.getDescription());
                image.setImageDate(dto.getImageDate());
                image.setProject(project);
                return image;
              }).toList();

      project.setProgressImages(images);
    }
    return project;
  }

  @Async("taskExecutor")
  @Transactional
  public void updateProjectProgressMetric(String projectId) {
    log.info("calculating progress metrics for project ID: {}", projectId);

    ConstructionProject project = constructionRepository.findById(projectId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Project record not found with ID: " + projectId));

    List<ProjectObjective> objectives = project.getObjectives();

    if (objectives == null || objectives.isEmpty()) {
      project.setOverallProgress(0.0);
      constructionRepository.save(project);
      return;
    }

    long achievedCount = objectives.stream()
        .filter(ProjectObjective::isCompleted)
        .count();

    long totalCount = objectives.size();
    double rawPercentage = ((double) achievedCount / totalCount) * 100;
    double roundedPercentage = Math.round(rawPercentage * 100.0) / 100.0;

    project.setOverallProgress(roundedPercentage);
    constructionRepository.save(project);

    log.info("Project '{}' progress metric synchronized successfully to: {}% ({}/{})",
        project.getConstructionName(), roundedPercentage, achievedCount, totalCount);
  }
}
