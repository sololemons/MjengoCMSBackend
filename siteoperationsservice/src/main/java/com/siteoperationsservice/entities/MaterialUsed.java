package com.siteoperationsservice.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "materials_used")
public class MaterialUsed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "material_name", nullable = false)
    private String materialName;

    @Column(name = "quantity_consumed", nullable = false)
    private Double quantityConsumed;

    @Column(name = "unit_of_measurement", nullable = false)
    private String unitOfMeasurement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_project_id", nullable = false)
    private ConstructionProject constructionProject;
}