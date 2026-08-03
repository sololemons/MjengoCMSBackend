package com.storekeeperservice.records;

public record MaterialResponseDto(
        Long id,
        String materialName,
        Long quantity,
        String denomination,
        Long minThreshold,
        String categoryName,
        String wareHouseName,
        String brandName
) {}