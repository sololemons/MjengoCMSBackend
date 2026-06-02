package com.storekeeperservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MaterialTrackingAuditDto {
    private Long auditId;
    private Long originalTrackingId;
    private String actionType;
    private String materialName;
    private String category;
    private long oldQuantity;
    private long newQuantity;
    private String reason;
    private String changedBy;
    private String changedAt;
}