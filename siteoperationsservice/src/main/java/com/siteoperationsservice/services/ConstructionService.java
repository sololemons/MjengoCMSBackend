package com.siteoperationsservice.services;

import com.siteoperationsservice.dtos.*;
import com.siteoperationsservice.entities.ConstructionProject;
import com.siteoperationsservice.entities.ProjectObjective;
import com.siteoperationsservice.entities.ProjectProgressImage;
import com.siteoperationsservice.exceptions.GeofenceViolationException;
import com.siteoperationsservice.exceptions.ImageMetadataViolationException;
import com.siteoperationsservice.exceptions.ResourceNotFoundException;
import com.siteoperationsservice.repositories.ConstructionRepository;
import com.siteoperationsservice.repositories.ProgressImagesRepository;
import com.siteoperationsservice.repositories.ProjectObjectiveRepository;
import com.siteoperationsservice.utilis.Helpers;
import com.siteoperationsservice.utilis.LocationExtractor;
import com.siteoperationsservice.utilis.Mappers;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import org.springframework.web.server.ResponseStatusException;

import static com.siteoperationsservice.utilis.Mappers.toDto;

@RequiredArgsConstructor
@Service
@Slf4j
public class ConstructionService {

  private final ConstructionRepository constructionRepository;
  private final Helpers helpers;
  private final ProjectObjectiveRepository projectObjectiveRepository;
  private final ProgressImagesRepository progressImagesRepository;
  private final CloudinaryStorageFile cloudinaryStorageFile;
  private final LocationExtractor locationExtractor;

  @Transactional
  public ConstructionDto createConstruction(ConstructionDto constructionDto) {

    log.info("Creating Construction Project with Data : {}", constructionDto);
    ConstructionProject project = helpers.createConstruction(constructionDto);
    constructionRepository.save(project);
    log.info("Construction Project created successfully with ID: {}", project.getId());

    return toDto(project);
  }

  public String updateConstructionFields(String constructionId,
      UpdateConstructionFieldsDto updateConstructionFieldsDto) {
    ConstructionProject project = constructionRepository.findById(constructionId).orElseThrow(() ->
        new ResourceNotFoundException("Construction project not found with ID: " + constructionId));

    project.setConstructionName(updateConstructionFieldsDto.getConstructionName());
    project.setDescription(updateConstructionFieldsDto.getDescription());

    constructionRepository.save(project);
    return "Construction project Details updated successfully.";
  }


  public String updateConstructionObjectives(String constructionId,
      Long objectiveId,
      UpdateObjectivesDto updateObjectivesDto) {
    ProjectObjective objectives = projectObjectiveRepository.
        findByIdAndConstructionProject_Id(objectiveId, constructionId).orElseThrow(() ->
            new ResourceNotFoundException(String.format("Objective not found with ID: " +
                "%s for Construction project: %s", objectiveId, constructionId)));

    objectives.setTitle(updateObjectivesDto.getTitle());
    objectives.setDescription(updateObjectivesDto.getDescription());
    objectives.setEstimatedDurationDays(updateObjectivesDto.getEstimatedDurationDays());

    projectObjectiveRepository.save(objectives);
    return "Construction project objective updated successfully.";

  }

  public String updateConstructionProgressImages(String constructionId,
      Long imageId,
      UpdateProgressImageDto updateProgressImageDto) {
    ProjectProgressImage projectProgressImage = progressImagesRepository.
        findByIdAndProject_Id(imageId, constructionId)
        .orElseThrow(() -> new ResourceNotFoundException(
            String.format("Progress image not found with ID: %s for Construction project: %s",
                imageId, constructionId)
        ));

    projectProgressImage.setDescription(updateProgressImageDto.getDescription());
    projectProgressImage.setImageDate(updateProgressImageDto.getImageDate());

    progressImagesRepository.save(projectProgressImage);
    return "Construction project progress image updated successfully.";
  }


  public String addConstructionProgressImages(AddProjectImagesDto addProjectImagesDto) {
    ConstructionProject project = constructionRepository.findById(
        addProjectImagesDto.getConstructionId()).orElseThrow(() ->
        new ResourceNotFoundException(
            String.format(
                "Progress image cannot be added; Construction project not found with ID: %s",
                addProjectImagesDto.getConstructionId())
        ));

    try {
      LocationExtractor.GeoCoordinates coordinates = locationExtractor.extractGPS(
          addProjectImagesDto.getImageFile().getInputStream()
      );

      if (coordinates == null) {
        throw new ImageMetadataViolationException(
            "Image does not contain GPS metadata. Please ensure the image has location data."
        );
      }

      boolean isWithinGeofence = locationExtractor.verifyGeofence(
          addProjectImagesDto.getConstructionId(),
          coordinates.latitude(),
          coordinates.longitude()
      );

      if (!isWithinGeofence) {
        throw new GeofenceViolationException(
            "Image location is outside the geofence of the construction project."
        );
      }

      String imageUrl = cloudinaryStorageFile.uploadImage(addProjectImagesDto.getImageFile());

      ProjectProgressImage newImage = ProjectProgressImage.builder()
          .imageUrl(imageUrl)
          .description(addProjectImagesDto.getDescription())
          .imageDate(addProjectImagesDto.getImageDate())
          .project(project)
          .build();

      progressImagesRepository.save(newImage);

      return "Progress image added successfully to construction project with ID: "
          + addProjectImagesDto.getConstructionId();

    } catch (IOException e) {
      log.error("File read failure for construction ID: {}",
          addProjectImagesDto.getConstructionId(), e);
      return "Failed to read image file: " + e.getMessage();

    } catch (GeofenceViolationException | ImageMetadataViolationException e) {
      throw e;

    } catch (RuntimeException e) {
      log.error("Unexpected operational failure during upload tracking", e);
      return "Failed to upload image: " + e.getMessage();
    }
  }

  public String deleteConstructionProgressImage(String constructionId, Long imageId) {
    ProjectProgressImage image = progressImagesRepository.findByIdAndProject_Id(imageId,
            constructionId)
        .orElseThrow(() -> new ResourceNotFoundException(
            String.format("Progress image not found with ID: %s for Construction project: %s",
                imageId, constructionId)
        ));

    progressImagesRepository.delete(image);
    return "Construction project progress image deleted successfully.";
  }

  public String deleteConstruction(String constructionId) {
    ConstructionProject project = constructionRepository.findById(constructionId).orElseThrow(() ->
        new ResourceNotFoundException(
            String.format("Construction project not found with ID: %s", constructionId)));

    constructionRepository.delete(project);
    return "Construction project deleted successfully.";

  }

  public String deleteConstructionObjective(String constructionId, Long objectiveId) {
    ProjectObjective objective = projectObjectiveRepository.
        findByIdAndConstructionProject_Id(objectiveId, constructionId).orElseThrow(() ->
            new ResourceNotFoundException(
                String.format("Construction project not found with ID: %s", constructionId)));

    projectObjectiveRepository.delete(objective);
    helpers.updateProjectProgressMetric(constructionId);
    return "Construction project objective deleted successfully.";

  }

  public void updateProjectProgressMetric(MarkObjectiveCompleteDto markObjectiveCompleteDto) {
    ProjectObjective objective = projectObjectiveRepository.
        findByIdAndConstructionProject_Id
            (markObjectiveCompleteDto.getObjectiveId(),
                markObjectiveCompleteDto.getConstructionId()).
        orElseThrow(() ->
            new ResourceNotFoundException(
                String.format("Construction project not found with ID: %s",
                    markObjectiveCompleteDto.getObjectiveId())));

    objective.setCompleted(true);
    objective.setDateCompleted(LocalDate.now());
    projectObjectiveRepository.save(objective);

    helpers.updateProjectProgressMetric(markObjectiveCompleteDto.getConstructionId());
  }

  @Transactional
  public List<ConstructionDto> fetchConstructionsByEngineerNames(String engineerEmail) {

    List<ConstructionProject> constructionProjects = constructionRepository.findByAssignedEngineerEmails(
        engineerEmail);

    if (constructionProjects.isEmpty()) {
      log.info("No construction projects found for engineer email: {}", engineerEmail);
    }

    return constructionProjects.stream()
        .map(Mappers::toDto)
        .toList();
  }

  @Transactional
  public String addConstructionObjectives(String constructionId,
      AddObjectivesDto addObjectivesDto) {
    ConstructionProject project = constructionRepository.findById(constructionId).orElseThrow(() ->
        new ResourceNotFoundException(
            String.format("Construction project not found with ID: %s", constructionId)));
    ProjectObjective newObjective = ProjectObjective.builder()
        .title(addObjectivesDto.getTitle())
        .description(addObjectivesDto.getDescription())
        .estimatedDurationDays(addObjectivesDto.getEstimatedDurationDays())
        .constructionProject(project)
        .build();
    project.getObjectives().add(newObjective);
    constructionRepository.save(project);
    helpers.updateProjectProgressMetric(constructionId);

    return "Construction project objectives added successfully.";
  }


  public void markObjectiveAsStarted(MarkObjectiveCompleteDto markObjectiveStartedDto) {

    ProjectObjective objective = projectObjectiveRepository.
        findByIdAndConstructionProject_Id
            (markObjectiveStartedDto.getObjectiveId(),
                markObjectiveStartedDto.getConstructionId()).
        orElseThrow(() ->
            new ResourceNotFoundException(
                String.format("Construction project not found with ID: %s",
                    markObjectiveStartedDto.getObjectiveId())));
    if (objective.getStartDate() != null) {
      throw new IllegalStateException("Objective has already been started.");
    }

    objective.setStartDate(LocalDate.now());
    projectObjectiveRepository.save(objective);
  }
}
