package com.storekeeperservice.services;

import com.storekeeperservice.dtos.MaterialMovementType;
import com.storekeeperservice.dtos.TrackingLedgerDto;
import com.storekeeperservice.entities.MaterialTracking;
import com.storekeeperservice.exceptions.UserNotFoundException;
import com.storekeeperservice.repositories.MaterialTrackingRepository;
import com.storekeeperservice.utilis.MapperDtos;
import com.storekeeperservice.utilis.MaterialTrackingSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReportsService {
    private final MaterialTrackingRepository materialTrackingRepository;
    private final MapperDtos mapperDtos;
    private final PdfReportService pdfReportService;

    public ByteArrayInputStream getFilteredLedgerPdf(Long materialId, MaterialMovementType type, LocalDateTime start, LocalDateTime end) {

        Specification<MaterialTracking> spec = Specification
                .where(MaterialTrackingSpecification.hasMaterialId(materialId))
                .and(MaterialTrackingSpecification.hasMovementType(type))
                .and(MaterialTrackingSpecification.isBetweenDates(start, end));

        List<MaterialTracking> results = materialTrackingRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "timestamp"));

        if (results.isEmpty()) {
            throw new UserNotFoundException("No movement records found for the selected filters.");
        }

        List<TrackingLedgerDto> dtos = results.stream()
                .map(mapperDtos::mapToDto)
                .toList();

        return pdfReportService.generateMaterialMovementReport(dtos);
    }
    }

