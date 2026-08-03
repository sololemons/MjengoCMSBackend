package com.storekeeperservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaterialDto {
    private Long materialId;
    private String materialName;
    private String categoryName;
    private String brandName;
    private long quantity;
    private String denomination;
    private Long minThreshold;
    private String wareHouseName;
    private Long categoryId;
    private LocalDateTime lastUpdated;
    private StockStatus stockStatus;
}

