package com.storekeeperservice.repositories;

import com.storekeeperservice.entities.MaterialTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialTrackingRepository extends JpaRepository<MaterialTracking, Long> {
}
