package com.siteoperationsservice.dtos;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MaterialUsedDto {
    private String materialName;
    private Double quantityConsumed;
    private String unitOfMeasurement;
}