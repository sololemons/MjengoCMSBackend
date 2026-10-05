package com.siteoperationsservice.dtos;

import lombok.Data;

@Data
public class AddObjectivesDto {

  private String title;
    private String description;
    private Integer estimatedDurationDays;

}
