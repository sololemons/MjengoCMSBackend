package com.siteoperationsservice.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MaterialUsedResponseDto {
    private Long id;
    private String materialName;
    private Double quantityConsumed;
    private String unitOfMeasurement;

}
