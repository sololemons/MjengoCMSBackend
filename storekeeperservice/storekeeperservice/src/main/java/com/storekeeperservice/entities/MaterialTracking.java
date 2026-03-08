package com.storekeeperservice.entities;

import com.storekeeperservice.dtos.MaterialMovementType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "material_tracking")
public class MaterialTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tracking_id")
    private Long trackingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Materials materials;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private MaterialMovementType materialMovementType;

    @Column(name = "quantity", nullable = false)
    private long quantity;

    @Column(name = "denomination")
    private String denomination;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "recorded_by")
    private String recordedBy;

    @Column(name = "issued_to")
    private String issuedTo;

    @Column(name = "receipt_file_url")
    private String receiptFileUrl;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Suppliers suppliers;


}