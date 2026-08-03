package com.siteoperationsservice.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "project_objectives")
public class ProjectObjective {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "title")
    private String title;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "estimated_duration_days")
    private Integer estimatedDurationDays;

    @Column(nullable = false, name = "is_completed")
    private boolean isCompleted;

    @Column(name = "date_completed")
    private LocalDate dateCompleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_project_id", nullable = false)
    private ConstructionProject constructionProject;

    @PrePersist
    public void initializeDefaults(){
        isCompleted = false;
    }

}