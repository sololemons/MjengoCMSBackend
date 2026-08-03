package com.siteoperationsservice.controllers;

import com.siteoperationsservice.services.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/site/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard}")
    public ResponseEntity<Map<String, Object>> getMostUsedMaterials(@RequestParam String constructionId, @RequestParam String timeFilter) {
        return ResponseEntity.ok(analyticsService.getProjectDashboardData(constructionId, timeFilter));
    }
}
