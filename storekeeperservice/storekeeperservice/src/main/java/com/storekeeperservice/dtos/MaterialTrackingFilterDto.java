package com.storekeeperservice.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MaterialTrackingFilterDto {
   private String materialName;
   private String supplierName;
   private String recordedBy;
    private String category;
    private MaterialMovementType movementType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

}
