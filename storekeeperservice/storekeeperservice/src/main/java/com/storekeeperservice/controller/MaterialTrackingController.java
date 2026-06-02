package com.storekeeperservice.controller;

import com.storekeeperservice.dtos.*;
import com.storekeeperservice.entities.MaterialTracking;
import com.storekeeperservice.services.MaterialTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class MaterialTrackingController {

    private final MaterialTrackingService materialTrackingService;

    @PostMapping(value = "/receive/delivery", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> receiveDelivery(
            @ModelAttribute("deliveryData") ReceiveDeliveryDto receiveDeliveryDto,
            @RequestParam(value = "receiptFile", required = false) MultipartFile receiptFile
    ) {

        String resultMessage = materialTrackingService.receiveDelivery(receiveDeliveryDto, receiptFile);

        return ResponseEntity.ok(resultMessage);
    }
    @GetMapping("/fetch/ledgers/filters")
    public ResponseEntity<Page<TrackingLedgerDto>> getMaterialLedgers(
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Long materialId,
            @RequestParam(required = false) MaterialMovementType movementType,
            @RequestParam(required = false) String recordedBy,
            @RequestParam(required = false) String timeFrame,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Page<TrackingLedgerDto> ledgers = materialTrackingService.getFilteredLedgers(
                supplierId, materialId, movementType, recordedBy, timeFrame, page, size
        );

        return ResponseEntity.ok(ledgers);
    }
    @PutMapping("/update/ledger")
    public ResponseEntity<TrackingLedgerDto> updateLedger(
            @RequestParam long trackingId, @RequestBody UpdateMaterialTrackingDto updateMaterialTrackingDto)
    {
        return ResponseEntity.ok(materialTrackingService.updateLedger(trackingId,updateMaterialTrackingDto));

    }
    @PatchMapping(value = "/upload/receipt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TrackingLedgerDto> uploadLateReceipt(
            @RequestParam long trackingId,
            @RequestPart("receiptFile") MultipartFile receiptFile
    ) {

        TrackingLedgerDto updatedLedger = materialTrackingService.uploadLateReceipt(trackingId, receiptFile);

        return ResponseEntity.ok(updatedLedger);
    }
    @PostMapping("/issue")
    public ResponseEntity<TrackingLedgerDto> issueMaterial(@RequestBody IssueMaterialDto dto) {

        TrackingLedgerDto issuedLedger = materialTrackingService.issueMaterial(dto);

        return ResponseEntity.ok(issuedLedger);
    }
    @DeleteMapping("/delete/ledger")
    public ResponseEntity<String> deleteLedger(@RequestParam long trackingId,@RequestParam String reason){
        String deleteResponse = materialTrackingService.deleteLedger(trackingId,reason);
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
        Page<MaterialTrackingAuditDto> auditLogs = materialTrackingService.getAuditLogsForLedger(trackingId, page, size);
        return ResponseEntity.ok(auditLogs);
    }
    @GetMapping("/get/issuance/lifecycle")
    public ResponseEntity<List<TrackingLedgerDto>> getIssuanceLifecycle(@RequestParam Long trackingId) {
        return ResponseEntity.ok(materialTrackingService.getIssuanceLifecycle(trackingId));
    }

}
