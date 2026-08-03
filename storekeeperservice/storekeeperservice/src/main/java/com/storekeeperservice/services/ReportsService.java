package com.storekeeperservice.services;

import com.storekeeperservice.dtos.MaterialMovementType;
import com.storekeeperservice.dtos.MaterialTrackingFilterDto;
import com.storekeeperservice.dtos.TrackingLedgerDto;
import com.storekeeperservice.entities.MaterialTracking;
import com.storekeeperservice.exceptions.UserNotFoundException; // Consider renaming to ResourceNotFoundException for clarity
import com.storekeeperservice.repositories.MaterialTrackingRepository;
import com.storekeeperservice.utilis.MapperDtos;
import com.storekeeperservice.utilis.MaterialTrackingSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReportsService {

    private final MaterialTrackingRepository materialTrackingRepository;
    private final MapperDtos mapperDtos;
    private final PdfReportService pdfReportService;

    public List<TrackingLedgerDto> getFilteredLedgerData(MaterialTrackingFilterDto filterDto) {

        Specification<MaterialTracking> spec = MaterialTrackingSpecification.filterMaterialTracking(filterDto);


        List<MaterialTracking> results = materialTrackingRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "timestamp"));

        if (results.isEmpty()) {
            log.info("No material tracking records found for the given filter criteria: {}", filterDto);
            return List.of();
        }

        return results.stream()
                .map(mapperDtos::mapToDto)
                .toList();
    }


    public ByteArrayInputStream downloadLedgerPdf(MaterialTrackingFilterDto filterDto) {

        List<TrackingLedgerDto> dtos = getFilteredLedgerData(filterDto);
        try {
            return pdfReportService.generatePdfStream(dtos);
        } catch (Exception e) {
            log.error("Failed to generate PDF stream for ledger report", e);
            throw new RuntimeException("Could not generate PDF document.");
        }
    }
}