package com.storekeeperservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiveDeliveryDto {
    private Long materialId;
    private Long supplierId;
    private Long quantityReceived;
    private String denomination;

}
