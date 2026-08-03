package com.siteoperationsservice.services;

import com.siteoperationsservice.interfaces.DashboardProjections;
import com.siteoperationsservice.repositories.AnalyticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public Map<String, Object> getProjectDashboardData(String constructionId, String timeFilter) {
        Map<String, Object> dashboardData = new HashMap<>();

        LocalDate endDate = LocalDate.now();
        LocalDate startDate;

        if (timeFilter == null) {
            timeFilter = "ALL";
        }

        startDate = switch (timeFilter.toUpperCase()) {
            case "TODAY" -> LocalDate.now();
            case "WEEK" ->
                    LocalDate.now().with(DayOfWeek.MONDAY);
            case "MONTH" ->
                    LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
            case "YEAR" ->
                    LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
            default ->
                    LocalDate.of(2000, 1, 1);
        };

        List<DashboardProjections.MaterialUsageProjection> topUsed = analyticsRepository.findTopUsedMaterials(
                constructionId,
                startDate,
                endDate,
                PageRequest.of(0, 5)
        );

        dashboardData.put("topUsedMaterials", topUsed);
        dashboardData.put("activeFilter", timeFilter.toUpperCase());

        return dashboardData;
    }
}
