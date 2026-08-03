package com.siteoperationsservice.dtos;

import com.siteoperationsservice.enums.AttendanceStatus;
import com.siteoperationsservice.enums.WorkerRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkerAttendanceResponseDto {
    private Long id;
    private String workerName;
    private WorkerRole workerRole;
    private Integer quantityReported;
    private AttendanceStatus status;
    private boolean isPremiumDay;

}
