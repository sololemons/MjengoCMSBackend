package com.storekeeperservice.entities;

import com.storekeeperservice.dtos.MachineryCondition;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(name = "machinery_condition")
    @Enumerated(EnumType.STRING)
    private MachineryCondition machineryCondition;
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
