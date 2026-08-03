package com.siteoperationsservice.dtos;

import com.siteoperationsservice.enums.AttendanceStatus;
import com.siteoperationsservice.enums.WorkerRole;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WorkerAttendanceDto {
    private WorkerRole workerRole;
    private Integer quantityReported;
    private AttendanceStatus status;
    private boolean isPremiumDay;
}