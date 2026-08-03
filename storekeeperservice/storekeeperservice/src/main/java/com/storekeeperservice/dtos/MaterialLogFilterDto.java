package com.storekeeperservice.dtos;

import lombok.Data;

@Data
public class MaterialLogFilterDto {
    private String materialName;
    private String categoryName;
    private String wareHouseName;
    private String stockStatus;
}
