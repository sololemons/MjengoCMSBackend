package com.siteoperationsservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjectObjectiveDto {
    private String description;
    private Long objectiveId;
    private String title;
    private Integer estimatedDurationDays;
    private boolean isCompleted;
    private LocalDate dateCompleted;

}
