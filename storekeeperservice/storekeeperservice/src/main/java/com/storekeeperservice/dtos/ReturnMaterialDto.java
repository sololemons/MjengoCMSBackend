package com.storekeeperservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnMaterialDto {
    private Long originalTrackingId;
    private long quantityReturned;
    private String returnedBy;
    private String machineryCondition;
}
