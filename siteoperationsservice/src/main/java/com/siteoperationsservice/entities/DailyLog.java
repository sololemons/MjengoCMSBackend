package com.siteoperationsservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "daily_logs")
public class DailyLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "construction_id", nullable = false)
    private String constructionId;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(name = "site_engineer_id", nullable = false)
    private String siteEngineerId;

    @Column(name = "is_log_sent", nullable = false)
    private boolean isLogSent;


    @OneToMany(mappedBy = "constructionProject", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "worker_log_id")
    private List<WorkerAttendance> workerAttendances;

    @OneToMany(mappedBy = "constructionProject", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "material_log_id")
    private List<MaterialUsed> materialsUsed;
}