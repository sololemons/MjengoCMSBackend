package com.storekeeperservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "material_tracking_audit")
public class MaterialTrackingAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "audit_id")
  private Long auditId;
  @Column(name = "original_tracking_id")
  private Long originalTrackingId;
  @Column(name = "action_type")
  private String actionType;
  @Column(name = "material_name")
  private String materialName;
  @Column(name = "category")
  private String category;
  @Column(name = "old_quantity")
  private long oldQuantity;
  @Column(name = "new_quantity")
  private long newQuantity;
  @Column(name = "old_supplier")
  private String oldSupplier;
  @Column(name = "new_supplier")
  private String newSupplier;
  @Column(name = "old_issuer")
  private String oldIssuer;
  @Column(name = "new_issuer")
  private String newIssuer;

  @Column(columnDefinition = "TEXT", name = "reason")
  private String reason;
  @Column(name = "changed_by")
  private String changedBy;
  @Column(name = "changed_at")
  private LocalDateTime changedAt;
}