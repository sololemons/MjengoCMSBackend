package com.storekeeperservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialRequestDto {
    private String materialName;
    private Long quantity;
    private String denomination;
    private String  brandName;
    private String wareHouseName;
    private Long categoryId;
    private Long minThreshold;

}