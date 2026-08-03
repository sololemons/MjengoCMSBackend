package com.siteoperationsservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "construction_projects")
public class ConstructionProject extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, name = "construction_name")
    private String constructionName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false,name = "latitude",updatable = false)
    private Double latitude;

    @Column(nullable = false,name = "longitude",updatable = false)
    private Double longitude;

    @Column(name = "allowed_radius_meters")
    private Double allowedRadiusMeters;

    @Column(name = "start_date",updatable = false)
    private LocalDate startDate;

    @Column(name = "estimated_end_date",updatable = false)
    private LocalDate estimatedEndDate;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Transient
    @Column(name = "overall_progress")
    private Double overallProgress;


    @OneToMany(mappedBy = "constructionProject", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ProjectObjective> objectives;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL,fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ProjectProgressImage> progressImages;


}