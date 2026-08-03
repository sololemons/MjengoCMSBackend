package com.storekeeperservice.dtos;

import com.storekeeperservice.dtos.TrackingLedgerDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MaterialHistoryResponseDto {
    private String materialName;
    private long netBalance;
    private String denomination;
    private List<TrackingLedgerDto> history;
}