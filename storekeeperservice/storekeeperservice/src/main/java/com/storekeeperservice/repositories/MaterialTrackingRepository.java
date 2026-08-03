package com.storekeeperservice.repositories;

import com.storekeeperservice.entities.MaterialTracking;
import com.storekeeperservice.interfaces.MaterialMovingProjection;
import com.storekeeperservice.interfaces.MaterialsUsedProjection;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialTrackingRepository extends JpaRepository<MaterialTracking, Long>,
    JpaSpecificationExecutor<MaterialTracking> {

  List<MaterialTracking> findByTrackingParentId(MaterialTracking parentEntry);

  @Query("SELECT m FROM MaterialTracking m " +
      "WHERE m.trackingId = :id OR m.trackingParentId.trackingId = :id " +
      "ORDER BY m.timestamp ASC")
  List<MaterialTracking> findFullLifecycle(@Param("id") Long id);

  List<MaterialTracking> findByMaterials_MaterialIdOrderByTimestampAsc(Long materialId);

  @Query("SELECT m.materialName AS materialName, " +
      "m.denomination AS denomination, " +
      "SUM(CASE " +
      "    WHEN mt.materialMovementType = 'ISSUED' THEN mt.quantity " +
      "    WHEN mt.materialMovementType = 'RETURNED' THEN -mt.quantity " +
      "    ELSE 0 END) AS quantity " +
      "FROM MaterialTracking mt " +
      "JOIN mt.materials m " +
      "WHERE m.materialId IN :materialIds " +
      "AND mt.timestamp BETWEEN :startDate AND :endDate " +
      "AND mt.materialMovementType IN ('ISSUED', 'RETURNED') " +
      "GROUP BY m.materialName, m.quantity, m.denomination")
  List<MaterialsUsedProjection> getNetMaterialsUsed(
      @Param("materialIds") List<Long> materialIds,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate
  );

  @Query("SELECT m.materialName AS materialName, " +
      "m.denomination AS denomination, " +
      "COUNT(mt.trackingId) AS frequency, " +
      "SUM(mt.quantity) AS totalQuantity " +
      "FROM MaterialTracking mt " +
      "JOIN mt.materials m " +
      "WHERE mt.materialMovementType = 'ISSUED' " +
      "AND mt.timestamp BETWEEN :startDate AND :endDate " +
      "GROUP BY  materialName,frequency,totalQuantity,denomination " +
      "ORDER BY COUNT(mt.trackingId) DESC")
  List<MaterialMovingProjection> getTopFastMovingMaterials(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      Pageable pageable
  );

  @Query("SELECT m.materialName AS materialName, " +
      "m.denomination AS denomination, " +
      "COUNT(mt.trackingId) AS frequency, " +
      "COALESCE(SUM(mt.quantity), 0) AS totalQuantity " +
      "FROM Materials m " +
      "LEFT JOIN MaterialTracking mt ON mt.materials = m " +
      "AND mt.materialMovementType = 'ISSUED' " +
      "AND mt.timestamp BETWEEN :startDate AND :endDate " +
      "WHERE m.quantity > 0 " +
      "GROUP BY m.materialId, m.materialName, m.denomination " +
      "ORDER BY COUNT(mt.trackingId) ASC")
  List<MaterialMovingProjection> getTopLowMovingMaterials(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      Pageable pageable
  );

}
