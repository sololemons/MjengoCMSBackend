package com.storekeeperservice.controller;

import com.storekeeperservice.dtos.IssueMaterialDto;
import com.storekeeperservice.dtos.MaterialHistoryResponseDto;
import com.storekeeperservice.dtos.MaterialTrackingAuditDto;
import com.storekeeperservice.dtos.MaterialTrackingFilterDto;
import com.storekeeperservice.dtos.ReceiveDeliveryDto;
import com.storekeeperservice.dtos.RecordDamageDto;
import com.storekeeperservice.dtos.ReturnMaterialDto;
import com.storekeeperservice.dtos.TrackingLedgerDto;
import com.storekeeperservice.dtos.UpdateMaterialTrackingDto;
import com.storekeeperservice.interfaces.MaterialMovingProjection;
import com.storekeeperservice.interfaces.MaterialsUsedProjection;
import com.storekeeperservice.services.MaterialTrackingService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class MaterialTrackingController {

  private final MaterialTrackingService materialTrackingService;

  @PostMapping(value = "/receive/delivery")
  public ResponseEntity<String> receiveDelivery(
      @RequestBody ReceiveDeliveryDto receiveDeliveryDto
  ) {

    String resultMessage = materialTrackingService.receiveDelivery(receiveDeliveryDto);

    return ResponseEntity.ok(resultMessage);
  }

  @GetMapping("/fetch/ledgers/filters")
  public ResponseEntity<Page<TrackingLedgerDto>> getMaterialLedgers(
      @ModelAttribute MaterialTrackingFilterDto filterDto,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {

    Page<TrackingLedgerDto> ledgers = materialTrackingService.getFilteredLedgers(filterDto, page,
        size);

    return ResponseEntity.ok(ledgers);
  }

  @PutMapping("/update/ledger")
  public ResponseEntity<TrackingLedgerDto> updateLedger(
      @RequestParam long trackingId,
      @RequestBody UpdateMaterialTrackingDto updateMaterialTrackingDto) {
    return ResponseEntity.ok(
        materialTrackingService.updateLedger(trackingId, updateMaterialTrackingDto));

  }

  @PatchMapping(value = "/upload/receipt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<TrackingLedgerDto> uploadLateReceipt(
      @RequestParam long trackingId,
      @RequestPart("receiptFile") MultipartFile receiptFile
  ) {

    TrackingLedgerDto updatedLedger = materialTrackingService.uploadLateReceipt(trackingId,
        receiptFile);

    return ResponseEntity.ok(updatedLedger);
  }

  @PostMapping("/issue")
  public ResponseEntity<TrackingLedgerDto> issueMaterial(@RequestBody IssueMaterialDto dto) {

    TrackingLedgerDto issuedLedger = materialTrackingService.issueMaterial(dto);

    return ResponseEntity.ok(issuedLedger);
  }

  @DeleteMapping("/delete/ledger")
  public ResponseEntity<String> deleteLedger(@RequestParam long trackingId,
      @RequestParam String reason) {
    String deleteResponse = materialTrackingService.deleteLedger(trackingId, reason);
    return ResponseEntity.ok(deleteResponse);
  }

  @PostMapping("/return")
  public ResponseEntity<TrackingLedgerDto> returnMaterial(@RequestBody ReturnMaterialDto dto) {
    TrackingLedgerDto returnedLedger = materialTrackingService.returnMaterial(dto);
    return ResponseEntity.ok(returnedLedger);
  }

  @PostMapping("/damage")
  public ResponseEntity<TrackingLedgerDto> recordDamage(@RequestBody RecordDamageDto dto) {

    TrackingLedgerDto lossLedger = materialTrackingService.recordDamageOrLoss(dto);

    return ResponseEntity.ok(lossLedger);
  }

  @GetMapping("/fetch/all/audit")
  public ResponseEntity<Page<MaterialTrackingAuditDto>> getAllAuditLogs(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Page<MaterialTrackingAuditDto> auditLogs = materialTrackingService.getAllAuditLogs(page, size);
    return ResponseEntity.ok(auditLogs);
  }

  @GetMapping("/audit/ledger/id")
  public ResponseEntity<Page<MaterialTrackingAuditDto>> getAuditLogsForLedger(
      @RequestParam long trackingId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Page<MaterialTrackingAuditDto> auditLogs = materialTrackingService.getAuditLogsForLedger(
        trackingId, page, size);
    return ResponseEntity.ok(auditLogs);
  }

  @GetMapping("/get/issuance/lifecycle")
  public ResponseEntity<List<TrackingLedgerDto>> getIssuanceLifecycle(
      @RequestParam Long trackingId) {
    return ResponseEntity.ok(materialTrackingService.getIssuanceLifecycle(trackingId));
  }

  @GetMapping("/material/history/id")
  public ResponseEntity<MaterialHistoryResponseDto> getMaterialHistory(
      @RequestParam Long materialId) {

    MaterialHistoryResponseDto historyResponse = materialTrackingService.getMaterialHistory(
        materialId);

    return ResponseEntity.ok(historyResponse);
  }


  @GetMapping("/material/used")
  public ResponseEntity<List<MaterialsUsedProjection>> getMaterialsUsed(
      @RequestParam List<Long> materialIds,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
    List<MaterialsUsedProjection> materialsUsed = materialTrackingService.getMaterialsUsed(
        materialIds,
        startDate, endDate);
    return ResponseEntity.ok(materialsUsed);
  }

  @GetMapping("/fast/moving/materials")
  public ResponseEntity<List<MaterialMovingProjection>> getFastMovingMaterials(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
    List<MaterialMovingProjection> fastMovingMaterials = materialTrackingService.getTopFastMovingMaterials(
        startDate, endDate);
    return ResponseEntity.ok(fastMovingMaterials);
  }

  @GetMapping("/dead/moving/materials")
  public ResponseEntity<List<MaterialMovingProjection>> getDeadMovingMaterials(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
    List<MaterialMovingProjection> deadMovingMaterials = materialTrackingService.getDeadMovingMaterials(
        startDate, endDate);
    return ResponseEntity.ok(deadMovingMaterials);
  }
}
