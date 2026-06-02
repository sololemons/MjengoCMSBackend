package com.storekeeperservice.services;

import com.storekeeperservice.dtos.*;
import com.storekeeperservice.entities.MaterialTracking;
import com.storekeeperservice.entities.MaterialTrackingAudit;
import com.storekeeperservice.entities.Materials;
import com.storekeeperservice.entities.Suppliers;
import com.storekeeperservice.exceptions.MissingFieldException;
import com.storekeeperservice.exceptions.UserNotFoundException;
import com.storekeeperservice.repositories.MaterialTrackingAuditRepository;
import com.storekeeperservice.repositories.MaterialTrackingRepository;
import com.storekeeperservice.repositories.MaterialsRepository;
import com.storekeeperservice.repositories.SuppliersRepository;
import com.storekeeperservice.utilis.MapperDtos;
import com.storekeeperservice.utilis.MaterialTrackingSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialTrackingService {
    private final MaterialTrackingRepository materialTrackingRepository;
    private final MaterialsRepository materialsRepository;
    private final SuppliersRepository suppliersRepository;
    private final MaterialTrackingAuditRepository materialTrackingAuditRepository;
    private final CloudinaryStorageFile cloudinaryStorageFile;
    private final MapperDtos mapperDtos;


    @Transactional
    public String receiveDelivery(ReceiveDeliveryDto dto, MultipartFile receiptFile) {

        log.info("Received Delivery from Supplier ID : {} For Material ID : {}", dto.getSupplierId(), dto.getMaterialId());
        String activeStorekeeperEmail = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        Materials material = materialsRepository.findById(dto.getMaterialId())
                .orElseThrow(() -> new UserNotFoundException("Material not found with ID: " + dto.getMaterialId()));

        Suppliers supplier = suppliersRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new UserNotFoundException("Supplier not found with ID: " + dto.getSupplierId()));

        String receiptUrl = null;
        if (receiptFile != null && !receiptFile.isEmpty()) {
            receiptUrl = cloudinaryStorageFile.uploadReceipt(receiptFile);
        }

        material.setQuantity(material.getQuantity() + dto.getQuantityReceived());
        material.setLastUpdated(LocalDateTime.now());
        materialsRepository.save(material);

        MaterialTracking trackingEntry = MaterialTracking.builder()
                .materials(material)
                .suppliers(supplier)
                .materialMovementType(MaterialMovementType.RECEIVED)
                .quantity(dto.getQuantityReceived())
                .denomination(dto.getDenomination())
                .timestamp(LocalDateTime.now())
                .recordedBy(activeStorekeeperEmail)
                .receiptFileUrl(receiptUrl)
                .build();

        materialTrackingRepository.save(trackingEntry);

        return "Successfully received " + dto.getQuantityReceived() + " " + dto.getDenomination() +
                " of " + material.getMaterialName() + " from " + supplier.getSupplierName();
    }
    @Transactional
    public Page<TrackingLedgerDto> getFilteredLedgers(
            Long supplierId, Long materialId, MaterialMovementType movementType,
            String recordedBy, String timeFrame, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());

        LocalDateTime startDate = null;
        LocalDateTime endDate = LocalDateTime.now();

        if (timeFrame != null) {
            startDate = switch (timeFrame.toUpperCase()) {
                case "TODAY" -> LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
                case "WEEK" -> LocalDateTime.now().minusDays(7);
                case "MONTH" -> LocalDateTime.now().minusMonths(1);
                default -> startDate;
            };
        }

        Specification<MaterialTracking> spec = Specification.where(
                        MaterialTrackingSpecification.hasSupplierId(supplierId))
                .and(MaterialTrackingSpecification.hasMaterialId(materialId))
                .and(MaterialTrackingSpecification.hasMovementType(movementType))
                .and(MaterialTrackingSpecification.hasRecordedBy(recordedBy))
                .and(MaterialTrackingSpecification.isBetweenDates(startDate, endDate));

        return materialTrackingRepository.findAll(spec, pageable).map(mapperDtos::mapToDto);
    }

    @Transactional
    public TrackingLedgerDto updateLedger(long trackingId, UpdateMaterialTrackingDto dto) {
        String activeUser = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        if (dto.getReason() == null || dto.getReason().isBlank()) {
            throw new MissingFieldException("You must provide a reason for editing this ledger entry.");
        }

        MaterialTracking existingLedger = materialTrackingRepository.findById(trackingId)
                .orElseThrow(() -> new IllegalArgumentException("Ledger not found."));

        Materials material = existingLedger.getMaterials();
        long oldQuantity = existingLedger.getQuantity();
        long newQuantity = (dto.getQuantity() != null) ? dto.getQuantity() : oldQuantity;

        MaterialTrackingAudit auditLog = MaterialTrackingAudit.builder()
                .originalTrackingId(existingLedger.getTrackingId())
                .actionType("UPDATE")
                .materialName(material.getMaterialName())
                .category(material.getCategory().getCategoryName())
                .oldQuantity(oldQuantity)
                .newQuantity(newQuantity)
                .reason(dto.getReason())
                .changedBy(activeUser)
                .changedAt(LocalDateTime.now())
                .build();

        materialTrackingAuditRepository.save(auditLog);

        if (existingLedger.getMaterialMovementType() == MaterialMovementType.RECEIVED) {
            material.setQuantity(material.getQuantity() - oldQuantity + newQuantity);
        } else if (existingLedger.getMaterialMovementType() == MaterialMovementType.ISSUED) {
            material.setQuantity(material.getQuantity() + oldQuantity - newQuantity);
        }

        materialsRepository.save(material);

        existingLedger.setQuantity(newQuantity);

        if (dto.getDenomination() != null && !dto.getDenomination().isBlank()) {
            existingLedger.setDenomination(dto.getDenomination());
        }

        if (dto.getIssuedTo() != null && !dto.getIssuedTo().isBlank()) {
            existingLedger.setIssuedTo(dto.getIssuedTo());
        }

        MaterialTracking savedLedger = materialTrackingRepository.save(existingLedger);

        return mapperDtos.mapToDto(savedLedger);
    }

    @Transactional
    public TrackingLedgerDto uploadLateReceipt(long trackingId, MultipartFile receiptFile) {

        MaterialTracking ledger = materialTrackingRepository.findById(trackingId)
                .orElseThrow(() -> new UserNotFoundException("Ledger not found with ID: " + trackingId));

        if (receiptFile == null || receiptFile.isEmpty()) {
            throw new MissingFieldException("Cannot upload an empty file. Please select a valid receipt.");
        }

        String newReceiptUrl = cloudinaryStorageFile.uploadReceipt(receiptFile);

        ledger.setReceiptFileUrl(newReceiptUrl);

        MaterialTracking savedLedger = materialTrackingRepository.save(ledger);
        return mapperDtos.mapToDto(savedLedger);
    }


    @Transactional
    public TrackingLedgerDto issueMaterial(IssueMaterialDto dto) {

        String activeStorekeeperEmail = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        Materials material = materialsRepository.findById(dto.getMaterialId())
                .orElseThrow(() -> new UserNotFoundException("Material not found with ID: " + dto.getMaterialId()));

        if (material.getQuantity() < dto.getQuantityIssued()) {
            throw new MissingFieldException("Insufficient stock! You are trying to issue "
                    + dto.getQuantityIssued() + " but only " + material.getQuantity() + " remain in the store.");
        }

        material.setQuantity(material.getQuantity() - dto.getQuantityIssued());
        material.setLastUpdated(LocalDateTime.now());
        materialsRepository.save(material);

        MaterialTracking trackingEntry = MaterialTracking.builder()
                .materials(material)
                .suppliers(null)
                .materialMovementType(MaterialMovementType.ISSUED)
                .quantity(dto.getQuantityIssued())
                .denomination(dto.getDenomination())
                .timestamp(LocalDateTime.now())
                .recordedBy(activeStorekeeperEmail)
                .issuedTo(dto.getIssuedTo())
                .receiptFileUrl(null)
                .build();

        MaterialTracking savedLedger = materialTrackingRepository.save(trackingEntry);

        return mapperDtos.mapToDto(savedLedger);
    }


    @Transactional
    public String deleteLedger(long trackingId, String deleteReason) {

        String activeUser = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        if (deleteReason == null || deleteReason.isBlank()) {
            throw new IllegalArgumentException("You must provide a reason for deleting this ledger entry.");
        }

        MaterialTracking ledger = materialTrackingRepository.findById(trackingId)
                .orElseThrow(() -> new IllegalArgumentException("Ledger not found with ID: " + trackingId));

        Materials material = ledger.getMaterials();

        MaterialTrackingAudit auditLog = MaterialTrackingAudit.builder()
                .originalTrackingId(ledger.getTrackingId())
                .actionType("DELETE")
                .materialName(material.getMaterialName())
                .oldQuantity(ledger.getQuantity())
                .newQuantity(0)
                .reason(deleteReason)
                .changedBy(activeUser)
                .changedAt(LocalDateTime.now())
                .build();
        materialTrackingAuditRepository.save(auditLog);

        if (ledger.getMaterialMovementType() == MaterialMovementType.RECEIVED) {
            material.setQuantity(material.getQuantity() - ledger.getQuantity());
        } else if (ledger.getMaterialMovementType() == MaterialMovementType.ISSUED) {
            material.setQuantity(material.getQuantity() + ledger.getQuantity());
        }
        materialsRepository.save(material);

        materialTrackingRepository.delete(ledger);

        return "Successfully deleted ledger entry " + trackingId + " and adjusted the master inventory.";
    }
    @Transactional
    public TrackingLedgerDto returnMaterial(ReturnMaterialDto dto) {
        String activeUser = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        MaterialTracking originalIssue = materialTrackingRepository.findById(dto.getOriginalTrackingId())
                .orElseThrow(() -> new UserNotFoundException("Original issuance record not found."));

        if (originalIssue.getMaterialMovementType() != MaterialMovementType.ISSUED) {
            throw new MissingFieldException("Returns must be linked to an 'ISSUED' transaction.");
        }

        long alreadyReturned = materialTrackingRepository.findByTrackingParentId(originalIssue)
                .stream()
                .filter(t -> t.getMaterialMovementType() == MaterialMovementType.RETURNED)
                .mapToLong(MaterialTracking::getQuantity)
                .sum();

        long remainingOutstanding = originalIssue.getQuantity() - alreadyReturned;

        // 4. Safety Check: Prevent returning more than the 70 issued
        if (dto.getQuantityReturned() > remainingOutstanding) {
            throw new MissingFieldException("Invalid return: You are trying to return " + dto.getQuantityReturned() +
                    " units, but only " + remainingOutstanding + " are outstanding from this issuance.");
        }

        Materials material = originalIssue.getMaterials();
        material.setQuantity(material.getQuantity() + dto.getQuantityReturned());

        if (dto.getMachineryCondition() != null && !dto.getMachineryCondition().isBlank()) {
            material.setMachineryCondition(MachineryCondition.valueOf(dto.getMachineryCondition()));
        }
        materialsRepository.save(material);

        MaterialTracking returnEntry = MaterialTracking.builder()
                .materials(material)
                .materialMovementType(MaterialMovementType.RETURNED)
                .quantity(dto.getQuantityReturned())
                .trackingParentId(originalIssue)
                .denomination(material.getDenomination())
                .timestamp(LocalDateTime.now())
                .recordedBy(activeUser)
                .issuedTo(dto.getReturnedBy())
                .build();

        MaterialTracking savedLedger = materialTrackingRepository.save(returnEntry);
        return mapperDtos.mapToDto(savedLedger);
    }

    @Transactional
    public TrackingLedgerDto recordDamageOrLoss(RecordDamageDto dto) {
        String activeUser = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        if (dto.getReason() == null || dto.getReason().isBlank()) {
            throw new MissingFieldException("A valid reason must be provided.");
        }

        MaterialTracking parentIssue = null;
        Materials material;

        if (dto.getParentTrackingId() != null) {
            parentIssue = materialTrackingRepository.findById(dto.getParentTrackingId())
                    .orElseThrow(() -> new IllegalArgumentException("Original issuance record not found."));

            material = parentIssue.getMaterials();

            long accountedFor = materialTrackingRepository.findByTrackingParentId(parentIssue).stream()
                    .mapToLong(MaterialTracking::getQuantity).sum();

            long remaining = parentIssue.getQuantity() - accountedFor;

            if (dto.getQuantityLost() > remaining) {
                throw new MissingFieldException("Cannot record " + dto.getQuantityLost() + " as lost. Only " +
                        remaining + " are outstanding for this issuance.");
            }
        }

        else {
            material = materialsRepository.findById(dto.getMaterialId())
                    .orElseThrow(() -> new UserNotFoundException("Material not found."));

            if (material.getQuantity() < dto.getQuantityLost()) {
                throw new MissingFieldException("Store only has " + material.getQuantity() + " units available.");
            }

            material.setQuantity(material.getQuantity() - dto.getQuantityLost());
            material.setLastUpdated(LocalDateTime.now());
            materialsRepository.save(material);
        }

        MaterialTracking trackingEntry = MaterialTracking.builder()
                .materials(material)
                .trackingParentId(parentIssue)
                .materialMovementType(MaterialMovementType.LOSS)
                .quantity(dto.getQuantityLost())
                .denomination(material.getDenomination())
                .timestamp(LocalDateTime.now())
                .recordedBy(activeUser)
                .reportedBy(dto.getReportedBy())
                .reasonForLoss(dto.getReason())
                .build();

        MaterialTracking savedLedger = materialTrackingRepository.save(trackingEntry);
        return mapperDtos.mapToDto(savedLedger);
    }


    public Page<MaterialTrackingAuditDto> getAllAuditLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("changedAt").descending());

        return materialTrackingAuditRepository.findAll(pageable)
                .map(mapperDtos::mapToAuditDto);
    }

    public Page<MaterialTrackingAuditDto> getAuditLogsForLedger(long trackingId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("changedAt").descending());

        return materialTrackingAuditRepository.findByOriginalTrackingId(trackingId, pageable)
                .map(mapperDtos::mapToAuditDto);
    }
    public List<TrackingLedgerDto> getIssuanceLifecycle(Long originalTrackingId) {
        List<MaterialTracking> lifecycle = materialTrackingRepository.findFullLifecycle(originalTrackingId);

        if (lifecycle.isEmpty()) {
            throw new UserNotFoundException("No history found for Tracking ID: " + originalTrackingId);
        }

        return lifecycle.stream()
                .map(mapperDtos::mapToDto)
                .collect(Collectors.toList());
    }
}
