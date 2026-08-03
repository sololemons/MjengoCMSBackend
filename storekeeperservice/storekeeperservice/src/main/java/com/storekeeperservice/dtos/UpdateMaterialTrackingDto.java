package com.storekeeperservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMaterialTrackingDto {
    private Long quantity;
    private String supplierName;
    private String denomination;
    private String issuedTo;
    private String reason;
}