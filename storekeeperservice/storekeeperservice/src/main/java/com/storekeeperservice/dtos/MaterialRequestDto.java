package com.storekeeperservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialRequestDto {
    private String materialName;
    private long quantity;
    private String denomination;
    private Long categoryId;
    private String machineryCondition;
    private Long minimumStockLevel;

}