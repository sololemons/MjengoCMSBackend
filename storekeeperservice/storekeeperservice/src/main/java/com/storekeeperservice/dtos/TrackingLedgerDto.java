package com.storekeeperservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrackingLedgerDto {
    private Long trackingId;
    private String materialName;
    private String category;
    private Long materialId;
    private String supplierName;
    private Long supplierId;
    private Long quantity;
    private String denomination;
    private String timestamp;
    private String recordedBy;
    private String reportedBy;
    private String reasonForLoss;
    private Long trackingParentId;
    private String receiptFileUrl;
    private String issuedTo;
    private String movementType;
}
