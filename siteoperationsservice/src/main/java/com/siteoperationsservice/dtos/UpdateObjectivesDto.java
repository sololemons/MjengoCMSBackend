package com.siteoperationsservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateObjectivesDto {
    private String title;
    private String description;
    private Integer estimatedDurationDays;
}
