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
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "suppliers")
public class Suppliers {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "supplier_id")
  private Long supplierId;

  @Column(name = "supplier_name", nullable = false)
  private String supplierName;

  @Column(name = "contact_person")
  private String contactPerson;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "supplier_email", unique = true, nullable = false)
  private String supplierEmail;

  @Column(name = "address")
  private String address;

  @UpdateTimestamp
  @Column(name = "last_updated")
  private LocalDateTime lastUpdated;


}