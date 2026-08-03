package com.siteoperationsservice.entities;

import com.siteoperationsservice.enums.AttendanceStatus;
import com.siteoperationsservice.enums.WorkerRole;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "worker_attendance")
public class WorkerAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "worker_role", nullable = false)
    private WorkerRole workerRole;

    @Column(name = "quantity_reported", nullable = false)
    private Integer quantityReported;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    @Column(name = "is_premium_day", nullable = false)
    private boolean isPremiumDay;

    @JoinColumn(name = "project_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ConstructionProject project;

}