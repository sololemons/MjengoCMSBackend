package com.storekeeperservice.controller;

import com.storekeeperservice.dtos.MaterialMovementType;
import com.storekeeperservice.dtos.TrackingLedgerDto;
import com.storekeeperservice.services.PdfReportService;
import com.storekeeperservice.services.ReportsService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RequestMapping("/store/reports")
@RestController
@RequiredArgsConstructor
public class ReportsController {
    private final ReportsService reportsService;
    private final PdfReportService pdfReportService;

    @GetMapping("/export/pdf")
    public ResponseEntity<InputStreamResource> downloadPdf(
            @RequestParam(required = false) Long materialId,
            @RequestParam(required = false) MaterialMovementType movementType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        ByteArrayInputStream bis = reportsService.getFilteredLedgerPdf(materialId, movementType, start, end);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=material_report_" + LocalDate.now() + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
