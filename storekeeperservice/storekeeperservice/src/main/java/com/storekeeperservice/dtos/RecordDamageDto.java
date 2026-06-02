package com.storekeeperservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordDamageDto {
        private Long materialId;
        private Long parentTrackingId;
        private Long quantityLost;
        private String reason;
        private String reportedBy;
    }

