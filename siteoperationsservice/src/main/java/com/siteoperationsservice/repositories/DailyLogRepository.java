package com.siteoperationsservice.repositories;

import com.siteoperationsservice.entities.DailyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyLogRepository extends JpaRepository<DailyLog, Long>, JpaSpecificationExecutor<DailyLog> {
    boolean existsByConstructionIdAndLogDate(String constructionId, LocalDate logDate);

  Optional<DailyLog> findByLogDateAndIsLogSent(LocalDate now, boolean b);
}
