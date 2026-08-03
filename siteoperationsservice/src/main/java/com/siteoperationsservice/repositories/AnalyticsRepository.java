package com.siteoperationsservice.repositories;

import com.siteoperationsservice.entities.DailyLog;

import com.siteoperationsservice.interfaces.DashboardProjections;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<DailyLog, Long> {

    @Query("SELECT m.materialName AS materialName, SUM(m.quantityConsumed) AS totalAmount " +
            "FROM DailyLog d JOIN d.materialsUsed m " +
            "WHERE d.constructionId = :constructionId " +
            "AND d.logDate BETWEEN :startDate AND :endDate " +
            "GROUP BY m.materialName " +
            "ORDER BY totalAmount DESC")
    List<DashboardProjections.MaterialUsageProjection> findTopUsedMaterials(
            @Param("constructionId") String constructionId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

}