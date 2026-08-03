package com.storekeeperservice.controller;


import com.storekeeperservice.dtos.MaterialTrackingFilterDto;
import com.storekeeperservice.dtos.TrackingLedgerDto;
import com.storekeeperservice.services.PdfReportService;
import com.storekeeperservice.services.ReportsService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

@RequestMapping("/store/reports")
@RestController
@RequiredArgsConstructor
public class ReportsController {

  private final ReportsService reportsService;
  private final PdfReportService pdfReportService;

  @GetMapping("/export/pdf")
  public ResponseEntity<InputStreamResource> downloadPdf(
      @ModelAttribute MaterialTrackingFilterDto filterDto) {

    ByteArrayInputStream bis = reportsService.downloadLedgerPdf(filterDto);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition",
        "attachment; filename=material_report_" + LocalDate.now() + ".pdf");

    return ResponseEntity.ok()
        .headers(headers)
        .contentType(MediaType.APPLICATION_PDF)
        .body(new InputStreamResource(bis));
  }

  @GetMapping("/ledger/data")
  public ResponseEntity<List<TrackingLedgerDto>> getLedgerData(
      @ModelAttribute MaterialTrackingFilterDto filterDto) {

    return ResponseEntity.ok(reportsService.getFilteredLedgerData(filterDto));
  }
}
