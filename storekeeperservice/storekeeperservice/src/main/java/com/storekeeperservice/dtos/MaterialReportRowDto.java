package com.storekeeperservice.dtos;

import com.storekeeperservice.dtos.MaterialMovementType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MaterialReportRowDto {
    private LocalDateTime date;
    private String materialName;
    private MaterialMovementType type;
    private long quantity;
    private String handledBy;
    private String reason;
    private Long referenceId;
}