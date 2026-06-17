package com.storekeeperservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "warehouses")
public class WareHouse {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "warehouse_name", nullable = false, unique = true)
    private String wareHouseName;

    @Column(name = "location", nullable = false)
    private String location;



}
