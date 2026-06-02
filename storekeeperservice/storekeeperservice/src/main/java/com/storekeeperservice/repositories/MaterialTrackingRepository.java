package com.storekeeperservice.repositories;

import com.storekeeperservice.entities.MaterialTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialTrackingRepository extends JpaRepository<MaterialTracking, Long>,
        JpaSpecificationExecutor<MaterialTracking> {
    List<MaterialTracking> findByTrackingParentId(MaterialTracking parentEntry);
    @Query("SELECT m FROM MaterialTracking m " +
            "WHERE m.trackingId = :id OR m.trackingParentId.trackingId = :id " +
            "ORDER BY m.timestamp ASC")
    List<MaterialTracking> findFullLifecycle(@Param("id") Long id);
}
