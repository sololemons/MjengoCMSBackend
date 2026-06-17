package com.storekeeperservice.entities;

import com.storekeeperservice.dtos.MachineryCondition;
import com.storekeeperservice.dtos.StockStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "materials")
public class Materials {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_id")
    private Long materialId;
    @Column(name = "material_name")
    private String materialName;
    @Column(name = "quantity")
    private long quantity;
    @Column(name = "denomination")
    private String denomination;
    @Column(name = "min_threshold")
    private Long minThreshold;
    @Column(name = "warehouse_id")
    private UUID warehouseId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(name = "machinery_condition")
    @Enumerated(EnumType.STRING)
    private MachineryCondition machineryCondition;
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();
    @Column(name = "stock_status")
    @Enumerated(EnumType.STRING)
    private StockStatus stockStatus;

}
