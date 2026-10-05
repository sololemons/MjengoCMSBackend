package com.siteoperationsservice.utilis;

import com.siteoperationsservice.dtos.DailyLogFilterDto;
import com.siteoperationsservice.entities.DailyLog;
import com.siteoperationsservice.entities.WorkerAttendance;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ConstructionSpecifications {


  public static Specification<DailyLog> filterLogs(DailyLogFilterDto filters) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (filters.getConstructionId() != null) {
        predicates.add(
            criteriaBuilder.equal(root.get("constructionId"), filters.getConstructionId()));
      }

      if (filters.getSiteEngineerEmail() != null) {
        predicates.add(
            criteriaBuilder.equal(root.get("siteEngineerEmail"), filters.getSiteEngineerEmail()));
      }

      if (filters.getLogDate() != null) {
        predicates.add(criteriaBuilder.equal(root.get("logDate"), filters.getLogDate()));
      }

      if (filters.getMonth() != null) {
        predicates.add(criteriaBuilder.equal(
            criteriaBuilder.function("MONTH", Integer.class, root.get("logDate")),
            filters.getMonth()
        ));
      }

      if (filters.getYear() != null) {
        predicates.add(criteriaBuilder.equal(
            criteriaBuilder.function("YEAR", Integer.class, root.get("logDate")),
            filters.getYear()
        ));
      }

      if (filters.getIsPremiumDay() != null) {
        Join<DailyLog, WorkerAttendance> workerJoin = root.join("workerAttendances");
        predicates.add(
            criteriaBuilder.equal(workerJoin.get("isPremiumDay"), filters.getIsPremiumDay()));
        assert query != null;
        query.distinct(true);
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

}
