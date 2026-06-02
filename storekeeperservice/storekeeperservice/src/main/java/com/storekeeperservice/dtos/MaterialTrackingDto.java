package com.storekeeperservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialTrackingDto {

    private String materialName;
    private String supplierName;
    private Long quantity;
    private String denomination;
    private String issuedTo;
    private String reason;
}
