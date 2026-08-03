package com.siteoperationsservice.dtos;


import lombok.*;

import java.time.LocalDate;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DailyLogRequestDto {
    private String constructionId;
    private LocalDate logDate;
    private Long siteEngineerId;
    private List<WorkerAttendanceDto> workerAttendances;
    private List<MaterialUsedDto> materialsUsed;
}

