package com.storekeeperservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueMaterialDto {
    private Long materialId;
    private long quantityIssued;
    private String denomination;
    private String issuedTo;
}
