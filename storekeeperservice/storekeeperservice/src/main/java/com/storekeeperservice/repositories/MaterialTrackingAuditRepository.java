package com.storekeeperservice.repositories;

import com.storekeeperservice.entities.MaterialTrackingAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialTrackingAuditRepository extends JpaRepository<MaterialTrackingAudit,Long> {
    Page<MaterialTrackingAudit> findByOriginalTrackingId(Long originalTrackingId, Pageable pageable);
}
