package com.siteoperationsservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ConstructionDto {
    private String constructionName;
    private String description;
    private String latitude;
    private String longitude;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double overallProgress;
    private LocalDate estimatedEndDate;
    private boolean isCompleted;
    private List<ProjectProgressImageDto> progressImages;
    private List<ProjectObjectiveDto> projectObjectives;
}
