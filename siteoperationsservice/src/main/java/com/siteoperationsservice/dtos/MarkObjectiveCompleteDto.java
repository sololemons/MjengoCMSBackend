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
public class MarkObjectiveCompleteDto {
    private String constructionId;
    private Long objectiveId;
}
