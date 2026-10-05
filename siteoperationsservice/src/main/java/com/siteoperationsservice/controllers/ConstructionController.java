package com.siteoperationsservice.controllers;

import com.siteoperationsservice.dtos.*;
import com.siteoperationsservice.services.ConstructionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/site")
@RequiredArgsConstructor
public class ConstructionController {

    private final ConstructionService constructionService;

    @PostMapping("/create/construction")
    public ResponseEntity<ConstructionDto> createConstruction(@RequestBody ConstructionDto constructionDto) {
        ConstructionDto savedConstruction = constructionService.createConstruction(constructionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedConstruction);
    }

    @PatchMapping("/update/construction/fields")
    public ResponseEntity<String> updateConstructionFields(@RequestParam String constructionId,
                                                           @RequestBody UpdateConstructionFieldsDto constructionDto) {
        String message = constructionService.updateConstructionFields(
                constructionId,
                constructionDto);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
    @PostMapping("/add/construction/objectives")
    public ResponseEntity<String> addConstructionObjectives(@RequestParam String constructionId,
                                                            @RequestBody AddObjectivesDto addObjectivesDto) {
        String message = constructionService.addConstructionObjectives(
                constructionId,
                addObjectivesDto);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @PatchMapping("/update/construction/objectives")
    public ResponseEntity<String> updateConstructionObjectives(@RequestParam String constructionId,
                                                               @RequestParam Long objectiveId,
                                                               @RequestBody UpdateObjectivesDto updateObjectivesDto
    ) {
        String message = constructionService.updateConstructionObjectives(
                constructionId,
                objectiveId,
                updateObjectivesDto
        );
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @PatchMapping("/update/progress/images")
    public ResponseEntity<String> updateConstructionProgressImages(@RequestParam String constructionId,
                                                                   @RequestParam Long imageId,
                                                                   @RequestBody UpdateProgressImageDto
                                                                           updateProgressImageDto) {
        String message = constructionService.updateConstructionProgressImages(
                constructionId,
                imageId,
                updateProgressImageDto);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @PostMapping(value = "/add/progress/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addConstructionProgressImages(
            @ModelAttribute AddProjectImagesDto addProjectImagesDto
    ) {
        String resultMessage = constructionService.addConstructionProgressImages(addProjectImagesDto);

        return ResponseEntity.status(HttpStatus.OK).body(resultMessage);
    }

    @DeleteMapping("/delete/progress/image")
    public ResponseEntity<String> deleteConstructionProgressImage(@RequestParam String constructionId,
                                                                  @RequestParam Long imageId) {
        String message = constructionService.deleteConstructionProgressImage(
                constructionId,
                imageId);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @DeleteMapping("/delete/construction")
    public ResponseEntity<String> deleteConstruction(@RequestParam String constructionId) {
        String message = constructionService.deleteConstruction(constructionId);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @DeleteMapping("/delete/construction/objective")
    public ResponseEntity<String> deleteConstructionObjective(@RequestParam String constructionId,
                                                              @RequestParam Long objectiveId) {
        String message = constructionService.deleteConstructionObjective(
                constructionId,
                objectiveId);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @PostMapping("/update/progress/metrics")
    public void markObjectiveAsCompleted(@RequestBody MarkObjectiveCompleteDto markObjectiveCompleteDto) {
        constructionService.updateProjectProgressMetric(markObjectiveCompleteDto);
    }
    @PostMapping("/start/objective")
    public void markObjectiveAsStarted(@RequestBody MarkObjectiveCompleteDto markObjectiveStartedDto) {
        constructionService.markObjectiveAsStarted(markObjectiveStartedDto);
    }
    @GetMapping("/fetch/construction/by/engineer/email")
    public ResponseEntity<List<ConstructionDto>> fetchConstructions(@RequestParam String engineerEmail) {
        List<ConstructionDto> constructionDto = constructionService.fetchConstructionsByEngineerNames(engineerEmail);
        return ResponseEntity.ok(constructionDto);
    }

}
