package com.storekeeperservice.utilis;

import com.storekeeperservice.dtos.CreateCategoryDto;
import com.storekeeperservice.dtos.MaterialDto;
import com.storekeeperservice.dtos.MaterialTrackingAuditDto;
import com.storekeeperservice.dtos.SupplierDto;
import com.storekeeperservice.dtos.TrackingLedgerDto;
import com.storekeeperservice.dtos.WareHouseDto;
import com.storekeeperservice.entities.Category;
import com.storekeeperservice.entities.MaterialTracking;
import com.storekeeperservice.entities.MaterialTrackingAudit;
import com.storekeeperservice.entities.Materials;
import com.storekeeperservice.entities.Suppliers;
import com.storekeeperservice.entities.WareHouse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MapperDtos {

  public static MaterialDto mapToDto(Materials material) {
    return MaterialDto.builder()
        .materialId(material.getMaterialId())
        .materialName(material.getMaterialName())
        .brandName(material.getBrandName())
        .categoryName(
            material.getCategory() != null ? material.getCategory().getCategoryName() : null)
        .quantity(material.getQuantity())
        .denomination(material.getDenomination())
        .minThreshold(material.getMinThreshold())
        .wareHouseName(
            material.getWareHouse() != null ? material.getWareHouse().getWareHouseName() : null)
        .categoryId(material.getCategory() != null ? material.getCategory().getCategoryId() : null)
        .lastUpdated(material.getLastUpdated())
        .stockStatus(material.getStockStatus())
        .build();
  }

  public CreateCategoryDto mapToDto(Category category) {
    CreateCategoryDto dto = new CreateCategoryDto();
    dto.setCategoryName(category.getCategoryName());
    dto.setCategoryDescription(category.getDescription());
    dto.setCategoryId(category.getCategoryId());
    return dto;
  }

  public List<CreateCategoryDto> mapCategoryListToDtoList(List<Category> categories) {
    return categories.stream()
        .map(this::mapToDto)
        .toList();
  }

  public SupplierDto mapToDto(Suppliers suppliers) {
    SupplierDto dto = new SupplierDto();
    dto.setSupplierName(suppliers.getSupplierName());
    dto.setSupplierId(suppliers.getSupplierId());
    dto.setAddress(suppliers.getAddress());
    dto.setPhoneNumber(suppliers.getPhoneNumber());
    dto.setSupplierEmail(suppliers.getSupplierEmail());
    dto.setContactPerson(suppliers.getContactPerson());
    return dto;
  }

  public List<SupplierDto> mapSuppliersListToDtoList(List<Suppliers> suppliers) {
    return suppliers.stream()
        .map(this::mapToDto)
        .toList();
  }

  public TrackingLedgerDto mapToDto(MaterialTracking ledger) {
    return TrackingLedgerDto.builder()
        .trackingId(ledger.getTrackingId())
        .materialId(ledger.getMaterials().getMaterialId())
        .materialName(ledger.getMaterials().getMaterialName())
        .category(ledger.getMaterials().getCategory().getCategoryName())
        .supplierId(ledger.getSuppliers() != null ? ledger.getSuppliers().getSupplierId() : null)
        .supplierName(
            ledger.getSuppliers() != null ? ledger.getSuppliers().getSupplierName() : "N/A")
        .movementType(ledger.getMaterialMovementType().name())
        .quantity(ledger.getQuantity())
        .denomination(ledger.getDenomination())
        .timestamp(String.valueOf(ledger.getTimestamp()))
        .recordedBy(ledger.getRecordedBy())
        .receiptFileUrl(ledger.getReceiptFileUrl())
        .reportedBy(ledger.getReportedBy())
        .reasonForLoss(ledger.getReasonForLoss())
        .issuedTo(ledger.getIssuedTo())
        .trackingParentId(
            ledger.getTrackingParentId() != null ? ledger.getTrackingParentId().getTrackingId()
                : null)
        .build();
  }

  public MaterialTrackingAuditDto mapToAuditDto(MaterialTrackingAudit audit) {
    return MaterialTrackingAuditDto.builder()
        .auditId(audit.getAuditId())
        .originalTrackingId(audit.getOriginalTrackingId())
        .actionType(audit.getActionType())
        .materialName(audit.getMaterialName())
        .category(audit.getCategory())
        .oldQuantity(audit.getOldQuantity())
        .newQuantity(audit.getNewQuantity())
        .oldIssuer(audit.getOldIssuer())
        .newIssuer(audit.getNewIssuer())
        .reason(audit.getReason())
        .changedBy(audit.getChangedBy())
        .changedAt(String.valueOf(audit.getChangedAt()))
        .build();
  }

  public WareHouseDto mapToDto(WareHouse wareHouse) {
    WareHouseDto dto = new WareHouseDto();
    dto.setWareHouseName(wareHouse.getWareHouseName());
    dto.setLocation(wareHouse.getLocation());
    return dto;
  }


}
