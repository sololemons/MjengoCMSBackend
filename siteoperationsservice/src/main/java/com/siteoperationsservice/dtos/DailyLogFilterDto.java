package com.siteoperationsservice.dtos;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DailyLogFilterDto {
    private Long constructionId;
    private Boolean isPremiumDay;
    private String siteEngineerEmail;
    private LocalDate logDate;
    private Integer month;
    private Integer year;
}