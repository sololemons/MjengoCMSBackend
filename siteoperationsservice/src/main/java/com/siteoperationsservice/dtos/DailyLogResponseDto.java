package com.siteoperationsservice.dtos;



import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class DailyLogResponseDto {
    private Long id;
    private String constructionId;
    private LocalDate logDate;
    private Long siteEngineerId;

    private List<WorkerAttendanceResponseDto> workerAttendances;
    private List<MaterialUsedResponseDto> materialsUsed;

}

